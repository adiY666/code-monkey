package com.monkey.gui;

import com.monkey.gui.components.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class EditorSidebar extends JPanel {

    private static final int WIDTH = 450;

    // Colors
    private static final Color BLUE = new Color(52, 152, 219);
    private static final Color PURPLE = new Color(155, 89, 182);
    private static final Color GREEN = new Color(46, 204, 113);
    private static final Color TEAL = new Color(0, 150, 136);

    // Editor Theme
    private static final Color EDITOR_BG = new Color(40, 44, 52);
    private static final Color EDITOR_TEXT = new Color(171, 178, 191);

    public final JTextArea codeArea;
    private final VisualMonkeyStudio studio;

    private JButton runBtn;

    public EditorSidebar(VisualMonkeyStudio studio) {
        this.studio = studio;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, 0));
        setBackground(UIConstants.BG_COLOR);

        // --- 1. TABS ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));
        tabs.setBackground(UIConstants.BG_COLOR);

        setupTabs(tabs);
        add(tabs, BorderLayout.NORTH);

        // --- 2. CODE AREA ---
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

        // --- 3. FOOTER ---
        setupFooter();
    }

    private void setupTabs(JTabbedPane tabs) {
        // Basic Tab
        JPanel basicInner = createInnerPanel(new Color(235, 245, 251));
        addToolBtn(basicInner, "⬆ STEP(50)", "step(50);\n", BLUE);
        addToolBtn(basicInner, "⬇ BACK(-50)", "step(-50);\n", BLUE);
        addToolBtn(basicInner, "↰ LEFT", "turn('left');\n", BLUE);
        addToolBtn(basicInner, "↱ RIGHT", "turn('right');\n", BLUE);

        // Loops Tab
        JPanel loopsInner = createInnerPanel(new Color(245, 238, 248));
        addToolBtn(loopsInner, "🔄 REPEAT 3", "for(int i = 0; i < 3; i++){\n\n}\n", PURPLE);
        addToolBtn(loopsInner, "❓ WHILE", "while(x < 300){\n\n}\n", PURPLE);

        // Vars & Vision Tab
        JPanel varsInner = createInnerPanel(new Color(233, 247, 239));
        addToolBtn(varsInner, "int x = 0", "int x = 0;\n", GREEN);
        addToolBtn(varsInner, "step(x)", "step(x);\n", GREEN);
        addToolBtn(varsInner, "📏 DISTANCE", "distanceTo('banana')", GREEN);
        addToolBtn(varsInner, "📐 ANGLE TO", "angleTo('banana')", GREEN);
        addToolBtn(varsInner, "👆 TOUCHING", "isTouched('banana')", GREEN);
        addToolBtn(varsInner, "👁 SIGHT", "isSeeing('banana')", GREEN);

        // Turtle Tab
        JPanel turtleInner = createInnerPanel(new Color(224, 242, 241));
        addToolBtn(turtleInner, "🐢 STEP(50)", "turtles[0].step(50);\n", TEAL);
        addToolBtn(turtleInner, "🐢 STEP(50)", "turtles[0].step(-50);\n", TEAL);
        addToolBtn(turtleInner, "🐢 ↰ LEFT", "turtles[0].turn('left');\n", TEAL);
        addToolBtn(turtleInner, "🐢 ↱ RIGHT", "turtles[0].turn('right');\n", TEAL);

        // Wrap the panels in scrolling wrappers and add to tabs!
        tabs.addTab("Basic", createScroll(basicInner, new Color(235, 245, 251)));
        tabs.addTab("Loops", createScroll(loopsInner, new Color(245, 238, 248)));
        tabs.addTab("Vars", createScroll(varsInner, new Color(233, 247, 239)));
        tabs.addTab("Turtle", createScroll(turtleInner, new Color(224, 242, 241)));

        tabs.setBackgroundAt(0, BLUE);
        tabs.setBackgroundAt(1, PURPLE);
        tabs.setBackgroundAt(2, GREEN);
        tabs.setBackgroundAt(3, TEAL);
        for(int i = 0; i < 4; i++) tabs.setForegroundAt(i, Color.WHITE);
    }

    // --- CHANGED: Sets up a standard inner grid for buttons ---
    private JPanel createInnerPanel(Color bg) {
        JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
        p.setBackground(bg);
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        return p;
    }

    // --- NEW: Wraps the inner panel in a JScrollPane so it doesn't stretch and can scroll ---
    private JScrollPane createScroll(JPanel inner, Color bg) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(bg);
        wrapper.add(inner, BorderLayout.NORTH); // Keeps the buttons at the top

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(WIDTH, 180)); // Height of the scroll area
        scroll.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling speed
        return scroll;
    }

    private void addToolBtn(JPanel p, String label, String code, Color color) {
        JButton b = new JButton(label);
        b.setFont(UIConstants.EMOJI_FONT.deriveFont(12f));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);

        // --- CHANGED: Force every button to always be exactly 40 pixels tall! ---
        b.setPreferredSize(new Dimension(0, 40));

        b.addActionListener(e -> {
            codeArea.insert(code, codeArea.getCaretPosition());
            codeArea.requestFocus();
        });
        p.add(b);
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
