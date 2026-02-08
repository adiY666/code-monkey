package com.monkey.gui;

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

public class EditorSidebar extends JPanel {

    private static final int WIDTH = 450;
    private static final int HEIGHT = 850;

    private static final Color BLUE = new Color(52, 152, 219);
    private static final Color PURPLE = new Color(155, 89, 182);
    private static final Color GREEN = new Color(46, 204, 113);
    private static final Color TEAL = new Color(0, 150, 136);
    private static final Color RUN_BTN_COLOR = new Color(39, 174, 96);
    private static final Color BACK_BTN_COLOR = new Color(149, 165, 166);
    private static final Color ORANGE = new Color(230, 126, 34);

    // Font that supports Emojis better
    private static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.BOLD, 12);
    // Better coding font
    private static final Font CODE_FONT = new Font("Consolas", Font.PLAIN, 16);

    public final JTextArea codeArea;
    private final VisualMonkeyStudio studio;

    public EditorSidebar(VisualMonkeyStudio studio) {
        this.studio = studio;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));
        setupTabs(tabs);
        add(tabs, BorderLayout.NORTH);

        codeArea = new JTextArea("# Write code to get bananas!\n", 20, 30);
        codeArea.setFont(CODE_FONT); // Better font
        codeArea.setBackground(new Color(40, 44, 52)); // Dark theme editor
        codeArea.setForeground(new Color(171, 178, 191));
        codeArea.setCaretColor(Color.WHITE);

        add(new JScrollPane(codeArea), BorderLayout.CENTER);

        setupFooter();
    }

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
        p.setOpaque(true);
        return p;
    }

    private void addToolBtn(JPanel p, String label, String code, Color color) {
        JButton b = new JButton(label);
        b.setFont(EMOJI_FONT); // Force emoji support
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.addActionListener(e -> codeArea.insert(code, codeArea.getCaretPosition()));
        p.add(b);
    }

    private void setupFooter() {
        JPanel footer = new JPanel(new GridLayout(3, 1, 5, 5));

        JButton runBtn = new JButton("▶ RUN CODE");
        runBtn.setFont(EMOJI_FONT);
        runBtn.setBackground(RUN_BTN_COLOR);
        runBtn.setForeground(Color.WHITE);
        runBtn.addActionListener(e -> studio.executeCode(codeArea.getText()));

        JButton restartBtn = new JButton("↺ TRY AGAIN");
        restartBtn.setFont(EMOJI_FONT);
        restartBtn.setBackground(ORANGE);
        restartBtn.setForeground(Color.WHITE);
        restartBtn.addActionListener(e -> studio.loadLevel(studio.getCurrentFile()));

        JButton menuBtn = new JButton("⬅ BACK TO MENU");
        menuBtn.setFont(EMOJI_FONT);
        menuBtn.setBackground(BACK_BTN_COLOR);
        menuBtn.setForeground(Color.WHITE);
        menuBtn.addActionListener(e -> studio.returnToMenu());

        footer.add(runBtn);
        footer.add(restartBtn);
        footer.add(menuBtn);
        add(footer, BorderLayout.SOUTH);
    }

}
