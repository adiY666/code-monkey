package com.monkey.gui;

import com.monkey.auth.User;
import com.monkey.gui.components.StudioHeader;
import com.monkey.gui.components.WinLoseDialogs;
import com.monkey.input.StudioMouseHandler;
import com.monkey.logic.CodeExecutor;
import com.monkey.logic.LevelManager;
import java.awt.BorderLayout;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class VisualMonkeyStudio extends JFrame implements ActionInterface {

    private final GameEnginePanel engine;
    private final AutoScrollPanel autoScrollWrapper;
    private final EditorSidebar sidebar;
    private final MapEditorTools mapTools;

    private final CodeExecutor executor;
    private final LevelManager levelManager;
    private final User currentUser;

    private String selectedTool = "none";
    private boolean isEditMode = false;

    private boolean isWinning = false;

    public VisualMonkeyStudio(User user) {
        this.currentUser = user;

        // Initialize Components
        this.engine = new GameEnginePanel();
        this.autoScrollWrapper = new AutoScrollPanel(engine);
        this.sidebar = new EditorSidebar(this);
        this.mapTools = new MapEditorTools(this);

        // Initialize Logic
        this.levelManager = new LevelManager(this, engine, autoScrollWrapper);
        this.executor = new CodeExecutor(engine, this::checkWinCondition);
        StudioMouseHandler mouseHandler = new StudioMouseHandler(this);
        StudioHeader header = new StudioHeader(this);

        // Listen for the exact moment the last banana is collected
        this.engine.setOnLevelComplete(this::onInstantWin);

        // Setup Frame
        setTitle("Visual Code Monkey Studio \uD83C\uDF4C - " + user.getUsername());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Setup Layout
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.add(mapTools, BorderLayout.WEST);
        leftContainer.add(sidebar, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(leftContainer, BorderLayout.WEST);

        JScrollPane scrollPane = new JScrollPane(autoScrollWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Setup Listeners
        engine.addMouseListener(mouseHandler);
        engine.addMouseMotionListener(mouseHandler);

        // Initial State
        mapTools.setVisible(false);
        sidebar.setVisible(true);
        clearCode();
    }

    public VisualMonkeyStudio(User user, File file) {
        this(user);
        if (file != null) {
            loadLevel(file);
        }
    }

    // --- Public Methods ---
    public User getCurrentUser() { return currentUser; }
    public LevelManager getLevelManager() { return levelManager; }
    public GameEnginePanel getEngine() { return engine; }
    public AutoScrollPanel getAutoScrollWrapper() { return autoScrollWrapper; }
    public String getSelectedTool() { return selectedTool; }
    public boolean isEditMode() { return isEditMode; }

    public File getCurrentFile() { return levelManager.currentFile; }

    public void setTool(String tool) {
        this.selectedTool = tool;
        engine.setRulerMode(0);
        if(tool.equals("Rotate") || tool.equals("Relative")) {
            engine.updateGhost("none", 0, 0);
        }
    }

    public void toggleEditMode(JButton btn) {
        isEditMode = !isEditMode;
        autoScrollWrapper.setEditorMode(isEditMode);

        if (isEditMode) {
            btn.setText("▶ TESTER");
            sidebar.setVisible(false);
            mapTools.setVisible(true);
        } else {
            btn.setText("📝 EDITOR");
            sidebar.setVisible(true);
            mapTools.setVisible(false);
            mapTools.resetSelection();
            selectedTool = "none";
            engine.updateGhost("none", 0, 0);
        }
        revalidate();
        repaint();
    }

    public void executeCode(String code) {
        engine.setRulerMode(0);
        isWinning = false;
        executor.execute(code);
    }

    public void stopCode() {
        executor.stop();
        engine.resetLevel();
        // --- FIX: Automatically reset the Run/Stop button visually! ---
        sidebar.setRunState(false);
    }

    public void clearCode() {
        sidebar.codeArea.setText("# Write code to get bananas!\n");
        sidebar.codeArea.setCaretPosition(sidebar.codeArea.getDocument().getLength());
        sidebar.codeArea.requestFocusInWindow();
    }

    public void loadLevel(File f) {
        levelManager.loadLevel(f);
        engine.setRulerMode(0);
        setVisible(true);
    }

    public void createNewLevel() {
        levelManager.createNewLevel();
        engine.setRulerMode(0);
    }

    public void returnToMenu() {
        this.dispose();
        new LevelMenu(currentUser).setVisible(true);
    }

    private int getEffectiveLineCount() {
        String code = sidebar.codeArea.getText();
        if (code == null || code.trim().isEmpty()) return 0;

        String[] lines = code.split("\n");
        int count = 0;
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !trimmed.startsWith("//")) {
                count++;
            }
        }
        return count;
    }

    private void onInstantWin() {
        if (isWinning) return;
        isWinning = true;

        executor.stop();
        // --- FIX: Reset the Run/Stop button when the monkey wins! ---
        sidebar.setRunState(false);

        SwingUtilities.invokeLater(() -> {
            int limit = levelManager.currentLevelLimit;
            int linesUsed = getEffectiveLineCount();
            int stars = (limit == 0 || linesUsed <= limit) ? 3 : (linesUsed > limit * 1.5 ? 1 : 2);

            levelManager.saveProgress(stars);
            WinLoseDialogs.showWin(this, stars);
        });
    }

    private void checkWinCondition(int linesUsed) {
        if (isWinning) return;

        // --- FIX: Reset the Run/Stop button when the code naturally finishes running! ---
        sidebar.setRunState(false);

        if (engine.getBananaCount() == 0) {
            onInstantWin();
        } else {
            WinLoseDialogs.showLose(this);
        }
    }

    @Override public void step(int d) {
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> engine.animateMove(d, latch::countDown));
        try { latch.await(); } catch (Exception ignored) {}
    }

    @Override public void turn(String d) { engine.rotateMonkey(d); }
}
