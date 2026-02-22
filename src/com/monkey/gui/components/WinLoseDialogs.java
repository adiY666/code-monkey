package com.monkey.gui.components;

import com.monkey.gui.VisualMonkeyStudio;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class WinLoseDialogs {

    public static void showWin(VisualMonkeyStudio context, int stars) {
        JDialog dialog = createDialog(context, 400, 350);
        JPanel panel = createPanel(UIConstants.BTN_GREEN);

        JLabel title = new JLabel("LEVEL COMPLETE!", SwingConstants.CENTER);
        title.setFont(UIConstants.TITLE_FONT);
        title.setForeground(UIConstants.BTN_GREEN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel starLbl = new JLabel("⭐".repeat(stars), SwingConstants.CENTER);
        starLbl.setFont(UIConstants.EMOJI_FONT.deriveFont(50f));
        starLbl.setForeground(Color.YELLOW);
        starLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel(stars == 3 ? "Perfect Code!" : "Good Job!", SwingConstants.CENTER);
        msg.setFont(UIConstants.TEXT_FONT.deriveFont(18f));
        msg.setForeground(Color.LIGHT_GRAY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btns.setOpaque(false);

        btns.add(createBtn("MENU", UIConstants.BTN_GRAY, e -> {
            dialog.dispose();
            context.returnToMenu();
        }));

        // --- ADDED STOP CODE HERE ---
        btns.add(createBtn("REDO", UIConstants.BTN_ORANGE, e -> {
            context.stopCode();
            dialog.dispose();
            context.loadLevel(context.getLevelManager().currentFile);
        }));

        btns.add(createBtn("NEXT ➡", UIConstants.BTN_BLUE, e -> {
            context.stopCode();
            dialog.dispose();
            context.clearCode();
            context.getLevelManager().loadNextLevel();
        }));

        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(starLbl);
        panel.add(Box.createVerticalStrut(10));
        panel.add(msg);
        panel.add(Box.createVerticalStrut(30));
        panel.add(btns);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void showLose(VisualMonkeyStudio context) {
        JDialog dialog = createDialog(context, 350, 300);
        JPanel panel = createPanel(UIConstants.BTN_RED);

        JLabel title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setFont(UIConstants.TITLE_FONT);
        title.setForeground(UIConstants.BTN_RED);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Empty Stars
        JLabel starLbl = new JLabel("☆☆☆", SwingConstants.CENTER);
        starLbl.setFont(UIConstants.EMOJI_FONT.deriveFont(50f));
        starLbl.setForeground(Color.GRAY);
        starLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("The Monkey is still hungry 🍌", SwingConstants.CENTER);
        msg.setFont(UIConstants.TEXT_FONT.deriveFont(16f));
        msg.setForeground(Color.LIGHT_GRAY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btns.setOpaque(false);

        // --- ADDED STOP CODE HERE ---
        btns.add(createBtn("TRY AGAIN", UIConstants.BTN_GREEN, e -> {
            context.stopCode();
            dialog.dispose();
            context.loadLevel(context.getLevelManager().currentFile);
        }));

        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(starLbl);
        panel.add(Box.createVerticalStrut(15));
        panel.add(msg);
        panel.add(Box.createVerticalStrut(30));
        panel.add(btns);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static JDialog createDialog(VisualMonkeyStudio context, int w, int h) {
        JDialog d = new JDialog(context, true);
        d.setUndecorated(true);
        d.setSize(w, h);
        d.setLocationRelativeTo(context);
        return d;
    }

    private static JPanel createPanel(Color borderColor) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UIConstants.BG_COLOR);
        p.setBorder(BorderFactory.createLineBorder(borderColor, 4));
        return p;
    }

    private static JButton createBtn(String text, Color bg, java.awt.event.ActionListener act) {
        JButton b = new JButton(text);
        b.setFont(UIConstants.EMOJI_FONT.deriveFont(Font.BOLD, 14f));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.addActionListener(act);
        return b;
    }
}
