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
    private final StudioHeader header;

    private final CodeExecutor executor;
    private final LevelManager levelManager;
    private final User currentUser;
    private final StudioMouseHandler mouseHandler;

    private String selectedTool = "none";
    private boolean isEditMode = false;

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
        this.mouseHandler = new StudioMouseHandler(this);
        this.header = new StudioHeader(this);

        // Setup Frame
        setTitle("Visual Code Monkey Studio 🍌 - " + user.getUsername());
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
        executor.execute(code);
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

    // --- ADDED MISSING METHOD ---
    public void createNewLevel() {
        levelManager.createNewLevel();
        engine.setRulerMode(0);
    }

    public void returnToMenu() {
        this.dispose();
        new LevelMenu(currentUser).setVisible(true);
    }

    private void checkWinCondition(int linesUsed) {
        if(engine.getBananaCount() == 0) {
            int limit = levelManager.currentLevelLimit;
            int stars = (linesUsed <= limit) ? 3 : (linesUsed > limit*2 ? 1 : 2);
            levelManager.saveProgress(stars);
            WinLoseDialogs.showWin(this, stars);
        } else {
            WinLoseDialogs.showLose(this);
        }
    }

    @Override public void step(int d) {
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> engine.animateMove(d, latch::countDown));
        try { latch.await(); } catch (Exception e) {}
    }

    @Override public void turn(String d) { engine.rotateMonkey(d); }
}
