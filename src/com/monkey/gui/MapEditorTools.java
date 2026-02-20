package com.monkey.gui;

import com.monkey.gui.components.UIConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

public class MapEditorTools extends JPanel {

    private static final int WIDTH = 450;

    // Use UIConstants for colors too if you want, or keep local
    private static final Color TOOLBAR_BG = new Color(44, 62, 80);
    private static final Color ACTIVE_TOOL_COLOR = new Color(46, 204, 113);
    private static final Color INACTIVE_TOOL_COLOR = new Color(52, 73, 94);

    private final VisualMonkeyStudio context;
    private final List<JToggleButton> buttons = new ArrayList<>();

    public MapEditorTools(VisualMonkeyStudio context) {
        this.context = context;

        setPreferredSize(new Dimension(WIDTH, 0));
        setBackground(TOOLBAR_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("EDITOR TOOLS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(30));

        addSectionLabel("PLACEMENTS");
        JPanel objPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        objPanel.setBackground(TOOLBAR_BG);
        objPanel.setMaximumSize(new Dimension(WIDTH, 200));

        addToolBtn(objPanel, "Banana", "🍌");
        addToolBtn(objPanel, "Stone", "🪨");
        addToolBtn(objPanel, "River", "🌊");
        addToolBtn(objPanel, "Turtle", "🐢");
        addToolBtn(objPanel, "Spawn", "🏁");

        add(objPanel);
        add(Box.createVerticalStrut(30));

        addSectionLabel("MANIPULATION");
        JPanel actPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        actPanel.setBackground(TOOLBAR_BG);
        actPanel.setMaximumSize(new Dimension(WIDTH, 100));

        addToolBtn(actPanel, "Rotate", "↻ ROTATE");
        addToolBtn(actPanel, "Relative", "⟷ MOVE");

        add(actPanel);
        add(Box.createVerticalGlue());

        setVisible(false);
    }

    private void addSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lbl);
        add(Box.createVerticalStrut(10));
    }

    private void addToolBtn(JPanel panel, String name, String label) {
        JToggleButton btn = new JToggleButton(label);
        btn.setToolTipText(name);

        // --- USE CENTRAL FONT HERE ---
        btn.setFont(UIConstants.EMOJI_FONT);

        btn.setForeground(Color.WHITE);
        btn.setBackground(INACTIVE_TOOL_COLOR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        btn.addActionListener(e -> {
            for (JToggleButton b : buttons) {
                b.setSelected(false);
                b.setBackground(INACTIVE_TOOL_COLOR);
            }
            btn.setSelected(true);
            btn.setBackground(ACTIVE_TOOL_COLOR);

            context.setTool(name);

            if (name.equals("Relative")) {
                JOptionPane.showMessageDialog(context, "Click Anchor Object, then Target Object.");
            }
        });

        buttons.add(btn);
        panel.add(btn);
    }

    public void resetSelection() {
        for (JToggleButton b : buttons) {
            b.setSelected(false);
            b.setBackground(INACTIVE_TOOL_COLOR);
        }
    }
}
