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

    // Colors from your example
    private static final Color BLUE = new Color(52, 152, 219);
    private static final Color PURPLE = new Color(155, 89, 182);
    private static final Color GREEN = new Color(46, 204, 113);
    private static final Color TEAL = new Color(0, 150, 136);

    // Editor Theme
    private static final Color EDITOR_BG = new Color(40, 44, 52);
    private static final Color EDITOR_TEXT = new Color(171, 178, 191);

    public final JTextArea codeArea;
    private final VisualMonkeyStudio studio;

    public EditorSidebar(VisualMonkeyStudio studio) {
        this.studio = studio;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, 0));
        setBackground(UIConstants.BG_COLOR);

        // --- 1. TABS (Matches your example) ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));
        tabs.setBackground(UIConstants.BG_COLOR);

        setupTabs(tabs);
        add(tabs, BorderLayout.NORTH);

        // --- 2. CODE AREA (Matches your example) ---
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

        // --- 3. FOOTER (Run/Reset only) ---
        setupFooter();
    }

    // --- YOUR EXACT CODE HERE ---
    private void setupTabs(JTabbedPane tabs) {
        JPanel basic = createTabPanel(new Color(235, 245, 251));
        addToolBtn(basic, "⬆ STEP(50)", "step(50);\n", BLUE);
        addToolBtn(basic, "⬇ BACK(-50)", "step(-50);\n", BLUE);
        addToolBtn(basic, "↰ LEFT", "turn('left');\n", BLUE);
        addToolBtn(basic, "↱ RIGHT", "turn('right');\n", BLUE);

        JPanel loops = createTabPanel(new Color(245, 238, 248));
        addToolBtn(loops, "🔄 REPEAT 3", "for(int i = 0; i < 3; i++){\n\n}\n", PURPLE);
        addToolBtn(loops, "❓ WHILE", "while(x < 300){\n\n}\n", PURPLE);

        JPanel vars = createTabPanel(new Color(233, 247, 239));
        addToolBtn(vars, "int x = 0", "int x = 0;\n", GREEN);
        addToolBtn(vars, "step(x)", "step(x);\n", GREEN);

        JPanel turtle = createTabPanel(new Color(224, 242, 241));
        addToolBtn(turtle, "🐢 STEP(50)", "turtles[0].step(50);\n", TEAL);
        addToolBtn(turtle, "🐢 STEP(50)", "turtles[0].step(-50);\n", TEAL);
        addToolBtn(turtle, "🐢 ↰ LEFT", "turtles[0].turn('left');\n", TEAL);
        addToolBtn(turtle, "🐢 ↱ RIGHT", "turtles[0].turn('right');\n", TEAL);

        tabs.addTab("Basic", basic);
        tabs.addTab("Loops", loops);
        tabs.addTab("Vars", vars);
        tabs.addTab("Turtle", turtle);

        tabs.setBackgroundAt(0, BLUE);
        tabs.setBackgroundAt(1, PURPLE);
        tabs.setBackgroundAt(2, GREEN);
        tabs.setBackgroundAt(3, TEAL);
        for(int i = 0; i < 4; i++) tabs.setForegroundAt(i, Color.WHITE);
    }

    private JPanel createTabPanel(Color bg) {
        JPanel p = new JPanel(new GridLayout(0, 1, 2, 2));
        p.setBackground(bg);
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        return p;
    }

    private void addToolBtn(JPanel p, String label, String code, Color color) {
        JButton b = new JButton(label);
        b.setFont(UIConstants.EMOJI_FONT.deriveFont(12f));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);

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

        JButton runBtn = new JButton("▶ RUN CODE");
        configBtn(runBtn, UIConstants.BTN_GREEN);
        runBtn.addActionListener(e -> studio.executeCode(codeArea.getText()));

        JButton restartBtn = new JButton("↺ TRY AGAIN");
        configBtn(restartBtn, UIConstants.BTN_ORANGE);
        restartBtn.addActionListener(e -> studio.loadLevel(studio.getCurrentFile()));

        footer.add(runBtn);
        footer.add(restartBtn);
        // Menu button removed

        add(footer, BorderLayout.SOUTH);
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
