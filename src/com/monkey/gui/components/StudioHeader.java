package com.monkey.gui.components;

import com.monkey.auth.User;
import com.monkey.gui.VisualMonkeyStudio;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StudioHeader extends JPanel {

    private final VisualMonkeyStudio context;

    public StudioHeader(VisualMonkeyStudio context) {
        this.context = context;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(UIConstants.HEADER_COLOR);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        setupButtons();
    }

    private void setupButtons() {
        // 1. MENU BUTTON
        addBtn("🏠 MENU", UIConstants.BTN_GRAY, e -> context.returnToMenu());
        add(Box.createRigidArea(new Dimension(15, 0)));

        User user = context.getCurrentUser();

        // 2. DEVELOPER TOOLS
        if(user.isDeveloper()) {
            JButton modeBtn = new JButton("📝 EDITOR");
            configBtn(modeBtn, null);
            modeBtn.addActionListener(e -> context.toggleEditMode(modeBtn));

            add(modeBtn);
            add(Box.createRigidArea(new Dimension(5, 0)));
            addBtn("💾 SAVE NEW", UIConstants.BTN_RED, e -> context.getLevelManager().saveLevelDialog());
            add(Box.createRigidArea(new Dimension(5, 0)));
            addBtn("🔄 UPDATE", UIConstants.BTN_ORANGE, e -> context.getLevelManager().updateLevelDialog());
            add(Box.createRigidArea(new Dimension(5, 0)));
            addBtn("⚙ SETTINGS", UIConstants.BTN_PURPLE, e -> context.getLevelManager().openAdminSettings());
        } else {
            JLabel label = new JLabel("PLAYER: " + user.getUsername());
            label.setForeground(Color.LIGHT_GRAY);
            add(label);
        }

        add(Box.createHorizontalGlue());

        // 3. MEASUREMENT & SIGHT TOOLS
        addBtn("📏 OBJ RULER", UIConstants.BTN_RULER, e -> context.getEngine().setRulerMode(1));
        add(Box.createRigidArea(new Dimension(5, 0)));
        addBtn("📐 FREE RULER", UIConstants.BTN_RULER, e -> context.getEngine().setRulerMode(2));

        // --- ADDED SIGHT TOOL BUTTON ---
        add(Box.createRigidArea(new Dimension(5, 0)));
        addBtn("👁 SHOW SIGHT", UIConstants.BTN_RULER, e -> context.getEngine().toggleSightTool());

        if(user.isDeveloper()) {
            add(Box.createRigidArea(new Dimension(10, 0)));
            addBtn("🗑 CLEAR MAP", Color.GRAY, e -> context.getLevelManager().createNewLevel());
        }
    }

    private void addBtn(String text, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        configBtn(btn, bg);
        btn.addActionListener(action);
        add(btn);
    }

    private void configBtn(JButton btn, Color bg) {
        btn.setFont(UIConstants.EMOJI_FONT); // Use the central font
        if(bg != null) {
            btn.setBackground(bg);
            btn.setForeground(Color.WHITE);
        }
        btn.setFocusPainted(false);
    }
}
