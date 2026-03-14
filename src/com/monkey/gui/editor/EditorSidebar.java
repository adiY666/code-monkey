package com.monkey.gui.editor;

import com.monkey.gui.UIConstants;
import com.monkey.gui.game.VisualMonkeyStudio;
import com.monkey.language.CommandRegistry;
import com.monkey.language.GameCommand;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class EditorSidebar extends JPanel {

    private static final int WIDTH = 450;
    private static final Color EDITOR_BG = new Color(40, 44, 52);
    private static final Color EDITOR_TEXT = new Color(171, 178, 191);

    public final JTextArea codeArea;
    private final VisualMonkeyStudio studio;
    private JButton runBtn;

    // Memory list to track buttons so we can hide/show them dynamically
    private final Map<GameCommand, JButton> commandButtons = new HashMap<>();

    public EditorSidebar(VisualMonkeyStudio studio) {
        this.studio = studio;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, 0));
        setBackground(UIConstants.BG_COLOR);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));
        tabs.setBackground(UIConstants.BG_COLOR);

        setupTabs(tabs);
        add(tabs, BorderLayout.NORTH);

        codeArea = new JTextArea("# Write code to get bananas!\n", 20, 30);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        codeArea.setBackground(EDITOR_BG);
        codeArea.setForeground(EDITOR_TEXT);
        codeArea.setCaretColor(Color.WHITE);
        codeArea.setTabSize(4);
        codeArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(codeArea);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setBackground(EDITOR_BG);
        add(scroll, BorderLayout.CENTER);

        setupFooter();
    }

    private void setupTabs(JTabbedPane tabs) {
        JPanel basicInner = createInnerPanel(new Color(235, 245, 251));
        addCommandBtn(basicInner, CommandRegistry.STEP_FWD);
        addCommandBtn(basicInner, CommandRegistry.STEP_BCK);
        addCommandBtn(basicInner, CommandRegistry.TURN_L);
        addCommandBtn(basicInner, CommandRegistry.TURN_R);

        JPanel loopsInner = createInnerPanel(new Color(245, 238, 248));
        addCommandBtn(loopsInner, CommandRegistry.LOOP_FOR);
        addCommandBtn(loopsInner, CommandRegistry.LOOP_WHILE);

        JPanel varsInner = createInnerPanel(new Color(233, 247, 239));
        addCommandBtn(varsInner, CommandRegistry.VAR_INT);
        addCommandBtn(varsInner, CommandRegistry.VAR_STEP);
        addCommandBtn(varsInner, CommandRegistry.DIST_TO);
        addCommandBtn(varsInner, CommandRegistry.ANG_TO);
        addCommandBtn(varsInner, CommandRegistry.IS_TOUCH);
        addCommandBtn(varsInner, CommandRegistry.IS_SEE);

        JPanel turtleInner = createInnerPanel(new Color(224, 242, 241));
        addCommandBtn(turtleInner, CommandRegistry.T_STEP_FWD);
        addCommandBtn(turtleInner, CommandRegistry.T_STEP_BCK);
        addCommandBtn(turtleInner, CommandRegistry.T_TURN_L);
        addCommandBtn(turtleInner, CommandRegistry.T_TURN_R);

        JPanel objectsInner = createInnerPanel(new Color(253, 242, 233));
        addCommandBtn(objectsInner, CommandRegistry.OBJ_BANANA);
        addCommandBtn(objectsInner, CommandRegistry.OBJ_STONE);
        addCommandBtn(objectsInner, CommandRegistry.OBJ_TURTLE);
        addCommandBtn(objectsInner, CommandRegistry.OBJ_RIVER);

        tabs.addTab("Basic", createScroll(basicInner, new Color(235, 245, 251)));
        tabs.addTab("Loops", createScroll(loopsInner, new Color(245, 238, 248)));
        tabs.addTab("Vars", createScroll(varsInner, new Color(233, 247, 239)));
        tabs.addTab("Turtle", createScroll(turtleInner, new Color(224, 242, 241)));
        tabs.addTab("Objects", createScroll(objectsInner, new Color(253, 242, 233)));

        tabs.setBackgroundAt(0, CommandRegistry.STEP_FWD.uiColor);
        tabs.setBackgroundAt(1, CommandRegistry.LOOP_FOR.uiColor);
        tabs.setBackgroundAt(2, CommandRegistry.VAR_INT.uiColor);
        tabs.setBackgroundAt(3, CommandRegistry.T_STEP_FWD.uiColor);
        tabs.setBackgroundAt(4, CommandRegistry.OBJ_BANANA.uiColor);

        for(int i = 0; i < 5; i++) tabs.setForegroundAt(i, Color.WHITE);
    }

    private JPanel createInnerPanel(Color bg) {
        JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
        p.setBackground(bg);
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        return p;
    }

    private JScrollPane createScroll(JPanel inner, Color bg) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(bg);
        wrapper.add(inner, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(WIDTH, 180));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private void addCommandBtn(JPanel p, GameCommand cmd) {
        JButton b = new JButton(cmd.uiLabel);
        b.setFont(UIConstants.EMOJI_FONT.deriveFont(12f));
        b.setBackground(cmd.uiColor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(0, 40));

        b.addActionListener(e -> {
            codeArea.insert(cmd.defaultCode, codeArea.getCaretPosition());
            codeArea.requestFocus();
        });

        p.add(b);
        commandButtons.put(cmd, b);
    }

    public void unlockCommandsForLevel(String currentPack, int currentLevelNumber) {
        for (Map.Entry<GameCommand, JButton> entry : commandButtons.entrySet()) {
            GameCommand cmd = entry.getKey();
            JButton btn = entry.getValue();

            // Ask the command logic if it should be unlocked!
            btn.setVisible(cmd.isUnlocked(currentPack, currentLevelNumber));
        }
        revalidate();
        repaint();
    }

    private void setupFooter() {
        JPanel footer = new JPanel(new GridLayout(2, 1, 5, 5));
        footer.setBackground(UIConstants.BG_COLOR);
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));

        runBtn = new JButton("▶ RUN CODE");
        configBtn(runBtn, UIConstants.BTN_GREEN);

        runBtn.addActionListener(e -> {
            if (runBtn.getText().equals("▶ RUN CODE")) {
                setRunState(true);
                studio.executeCode(codeArea.getText());
            } else {
                setRunState(false);
                studio.stopCode();
            }
        });

        JButton restartBtn = new JButton("↺ TRY AGAIN");
        configBtn(restartBtn, UIConstants.BTN_ORANGE);

        restartBtn.addActionListener(e -> {
            studio.stopCode();
            studio.loadLevel(studio.getCurrentFile());
            setRunState(false);
        });

        footer.add(runBtn);
        footer.add(restartBtn);
        add(footer, BorderLayout.SOUTH);
    }

    public void setRunState(boolean isRunning) {
        if (isRunning) {
            runBtn.setText("⏹ STOP CODE");
            runBtn.setBackground(UIConstants.BTN_RED);
        } else {
            runBtn.setText("▶ RUN CODE");
            runBtn.setBackground(UIConstants.BTN_GREEN);
        }
    }

    private void configBtn(JButton btn, Color bg) {
        btn.setFont(UIConstants.TITLE_FONT.deriveFont(18f));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 50));
    }
}
