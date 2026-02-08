package com.monkey.gui;

import com.monkey.auth.User;
import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import com.monkey.logic.CodeExecutor;
import com.monkey.logic.LevelManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import javax.swing.*;

public class VisualMonkeyStudio extends JFrame implements ActionInterface {

    private static final String TITLE = "Visual Code Monkey Studio 🍌";
    private static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.BOLD, 12);
    private static final Color HEADER_COLOR = new Color(52, 73, 94);
    private static final Color SAVE_BTN_COLOR = new Color(231, 76, 60); // Red
    private static final Color UPDATE_BTN_COLOR = new Color(230, 126, 34); // Orange
    private static final Color NEW_BTN_COLOR = new Color(46, 204, 113);
    private static final Color RULER_BTN_COLOR = new Color(243, 156, 18);
    private static final Color SETTINGS_BTN_COLOR = new Color(142, 68, 173);

    public final GameEnginePanel engine;
    private final AutoScrollPanel autoScrollWrapper;
    private final EditorSidebar sidebar;
    private final MapEditorTools mapTools;

    private final CodeExecutor executor;
    private final LevelManager levelManager;
    private final User currentUser;

    private String selectedTool = "none";
    private boolean isEditMode = false;
    private Object relativeAnchor = null;

    public VisualMonkeyStudio(User user) {
        this.currentUser = user;

        this.engine = new GameEnginePanel();
        this.autoScrollWrapper = new AutoScrollPanel(engine);

        this.sidebar = new EditorSidebar(this);
        this.levelManager = new LevelManager(this, engine, autoScrollWrapper);
        this.executor = new CodeExecutor(engine, this::checkWinCondition);

        this.mapTools = new MapEditorTools(this);

        setTitle(TITLE + " - " + user.getUsername() + " [" + user.getRole() + "]");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupHeader();
        setupMouseListeners();

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.add(mapTools, BorderLayout.WEST);
        leftContainer.add(sidebar, BorderLayout.CENTER);

        add(leftContainer, BorderLayout.WEST);

        JScrollPane scrollPane = new JScrollPane(autoScrollWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        mapTools.setVisible(false);
        sidebar.setVisible(true);

        clearCode();
    }

    public User getCurrentUser() { return currentUser; }

    public void setTool(String tool) {
        this.selectedTool = tool;
        engine.setRulerMode(0);
        if(tool.equals("Rotate") || tool.equals("Relative")) {
            engine.updateGhost("none", 0, 0);
        }
    }

    private void setupMouseListeners() {
        MouseAdapter mouseLogic = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { handleMouse(e); }
            @Override
            public void mouseDragged(MouseEvent e) { handleMove(e); handleMouse(e); }
            @Override
            public void mouseMoved(MouseEvent e) { handleMove(e); }

            private void handleMove(MouseEvent e) {
                if(isEditMode) {
                    engine.updateGhost(selectedTool, e.getX(), e.getY());
                }
            }

            private void handleMouse(MouseEvent e) {
                if(engine.getRulerMode() > 0) {
                    if(SwingUtilities.isLeftMouseButton(e)) engine.handleRulerClick(e.getX(), e.getY());
                    else engine.setRulerMode(0);
                    return;
                }
                if(!isEditMode) return;

                if(SwingUtilities.isRightMouseButton(e)) {
                    engine.removeObject(e.getX(), e.getY());
                    autoScrollWrapper.updateMapSize();
                    return;
                }

                boolean changed = false;
                if(selectedTool.equals("Rotate")) { handleRotateTool(e.getX(), e.getY()); changed=true; }
                else if(selectedTool.equals("Relative")) { handleRelativeTool(e.getX(), e.getY()); changed=true; }
                else if(!selectedTool.equals("none")) {
                    engine.addObject(selectedTool, e.getX(), e.getY());
                    changed = true;
                }

                if(changed) autoScrollWrapper.updateMapSize();
            }
        };
        engine.addMouseListener(mouseLogic);
        engine.addMouseMotionListener(mouseLogic);
    }

    private void toggleEditMode(JButton btn) {
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

    private void openRelativeDialog(Object anchor, Object target) {
        JPanel p = new JPanel(new GridLayout(2, 2));
        JTextField d = new JTextField("100"); JTextField a = new JTextField("0");
        p.add(new JLabel("Dist:")); p.add(d); p.add(new JLabel("Ang:")); p.add(a);
        if(JOptionPane.showConfirmDialog(this, p, "Set", JOptionPane.OK_CANCEL_OPTION)==0) {
            try {
                double dist = Double.parseDouble(d.getText());
                double ang = Math.toRadians(Double.parseDouble(a.getText()));
                double ax = (anchor instanceof GameObject) ? ((GameObject)anchor).x : engine.monkeyX;
                double ay = (anchor instanceof GameObject) ? ((GameObject)anchor).y : engine.monkeyY;
                double nx = ax + dist * Math.cos(ang);
                double ny = ay - dist * Math.sin(ang);
                if(target instanceof GameObject) { ((GameObject)target).x = nx; ((GameObject)target).y = ny; }
                else { engine.monkeyX = nx; engine.monkeyY = ny; }
                engine.repaint();
                autoScrollWrapper.updateMapSize();
            } catch(Exception e){}
        }
    }

    public void executeCode(String code) { engine.setRulerMode(0); executor.execute(code); }
    public void clearCode() { sidebar.codeArea.setText("# Write code to get bananas!\n"); sidebar.codeArea.setCaretPosition(sidebar.codeArea.getDocument().getLength()); sidebar.codeArea.requestFocusInWindow(); }
    public void loadLevel(File f) { levelManager.loadLevel(f); engine.setRulerMode(0); setVisible(true); }
    public void createNewLevel() { levelManager.createNewLevel(); engine.setRulerMode(0); }
    private void checkWinCondition(int linesUsed) {
        if(engine.getBananaCount() == 0) {
            int limit = levelManager.currentLevelLimit;
            int stars = (linesUsed <= limit) ? 3 : (linesUsed > limit*2 ? 1 : 2);
            levelManager.saveProgress(stars); showWinScreen(stars);
        } else showLoseScreen();
    }
    private void showWinScreen(int stars) {
        int res = JOptionPane.showOptionDialog(this, "WIN! Stars: "+stars, "Win", 0, 1, null, new Object[]{"Next", "Redo", "Menu"}, "Next");
        if(res==0) { clearCode(); levelManager.loadNextLevel(); }
        else if(res==1) loadLevel(levelManager.currentFile);
        else returnToMenu();
    }
    private void showLoseScreen() {
        int res = JOptionPane.showOptionDialog(this, "Failed!", "Lose", 0, 0, null, new Object[]{"Retry", "Menu"}, "Retry");
        if(res==0) loadLevel(levelManager.currentFile); else returnToMenu();
    }

    private void setupHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setBackground(HEADER_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if(currentUser.isDeveloper()) {
            JButton modeBtn = new JButton("📝 EDITOR");
            modeBtn.setFont(EMOJI_FONT);
            modeBtn.addActionListener(e -> toggleEditMode(modeBtn));

            JButton saveBtn = new JButton("💾 SAVE NEW");
            saveBtn.setFont(EMOJI_FONT);
            saveBtn.setBackground(SAVE_BTN_COLOR);
            saveBtn.setForeground(Color.WHITE);
            saveBtn.addActionListener(e -> levelManager.saveLevelDialog());

            // --- NEW UPDATE BUTTON ---
            JButton updateBtn = new JButton("🔄 UPDATE");
            updateBtn.setFont(EMOJI_FONT);
            updateBtn.setBackground(UPDATE_BTN_COLOR);
            updateBtn.setForeground(Color.WHITE);
            updateBtn.addActionListener(e -> levelManager.updateLevelDialog());

            JButton settingsBtn = new JButton("⚙ SETTINGS");
            settingsBtn.setFont(EMOJI_FONT);
            settingsBtn.setBackground(SETTINGS_BTN_COLOR);
            settingsBtn.setForeground(Color.WHITE);
            settingsBtn.addActionListener(e -> levelManager.openAdminSettings());

            header.add(modeBtn);
            header.add(Box.createRigidArea(new Dimension(5, 0)));
            header.add(saveBtn);
            header.add(Box.createRigidArea(new Dimension(5, 0)));
            header.add(updateBtn);
            header.add(Box.createRigidArea(new Dimension(5, 0)));
            header.add(settingsBtn);
        } else {
            JLabel label = new JLabel("PLAYER: " + currentUser.getUsername());
            label.setForeground(Color.LIGHT_GRAY);
            header.add(label);
        }

        header.add(Box.createHorizontalGlue());

        JButton objRuler = new JButton("📏 OBJ RULER");
        objRuler.setFont(EMOJI_FONT);
        objRuler.setBackground(RULER_BTN_COLOR);
        objRuler.setForeground(Color.WHITE);
        objRuler.addActionListener(e -> engine.setRulerMode(1));

        JButton freeRuler = new JButton("📐 FREE RULER");
        freeRuler.setFont(EMOJI_FONT);
        freeRuler.setBackground(RULER_BTN_COLOR);
        freeRuler.setForeground(Color.WHITE);
        freeRuler.addActionListener(e -> engine.setRulerMode(2));

        header.add(objRuler);
        header.add(Box.createRigidArea(new Dimension(5, 0)));
        header.add(freeRuler);

        if(currentUser.isDeveloper()) {
            JButton clearBtn = new JButton("🗑 CLEAR MAP");
            clearBtn.setFont(EMOJI_FONT);
            clearBtn.setBackground(Color.GRAY);
            clearBtn.setForeground(Color.WHITE);
            clearBtn.addActionListener(e -> levelManager.createNewLevel());

            header.add(Box.createRigidArea(new Dimension(5, 0)));
            header.add(clearBtn);
        }

        add(header, BorderLayout.NORTH);
    }

    private void handleRotateTool(int x, int y) {
        Object obj = engine.getGameObjectAt(x, y);
        if(obj != null) {
            String val = JOptionPane.showInputDialog(this, "Angle (0-360):");
            try { if(val!=null) {
                double a = Double.parseDouble(val);
                if(obj instanceof Turtle) ((Turtle)obj).angle = a;
                else if(obj.equals("Monkey")) engine.monkeyAngle = a;
                engine.repaint();
            }} catch(Exception e){}
        }
    }
    private void handleRelativeTool(int x, int y) {
        Object obj = engine.getGameObjectAt(x, y);
        if(obj == null) return;
        if(relativeAnchor == null) { relativeAnchor = obj; JOptionPane.showMessageDialog(this, "Anchor Selected"); }
        else { openRelativeDialog(relativeAnchor, obj); relativeAnchor = null; }
    }
    public void returnToMenu() { this.dispose(); new LevelMenu(currentUser).setVisible(true); }
    @Override public void step(int d) {
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> engine.animateMove(d, latch::countDown));
        try { latch.await(); } catch (Exception e) {}
    }
    @Override public void turn(String d) { engine.rotateMonkey(d); }
    public File getCurrentFile() { return levelManager.currentFile; }
}
