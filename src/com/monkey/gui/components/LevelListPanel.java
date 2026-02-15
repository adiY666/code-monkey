package com.monkey.gui.components;

import com.monkey.auth.User;
import com.monkey.gui.LevelMenu;
import com.monkey.gui.VisualMonkeyStudio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class LevelListPanel extends JPanel {

    public LevelListPanel(LevelMenu context, User user, String path, Color categoryColor) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.BG_COLOR);

        File folder = new File(path);
        File[] files = folder.listFiles((d, n) -> n.endsWith(".json"));

        JPanel list = new JPanel(new GridLayout(0, 1, 10, 10));
        list.setBackground(UIConstants.BG_COLOR);

        if (files != null) {
            Arrays.sort(files);
            boolean unlocked = true;

            for (File f : files) {
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);

                int stars = user.getStars(f.getName());
                String sStr = stars > 0 ? " (" + "⭐".repeat(stars) + ")" : "";

                boolean isLocked = !unlocked && !user.isDeveloper();
                String btnText = isLocked ? "🔒 LOCKED" : "📄 " + f.getName().replace(".json", "") + sStr;

                JButton btn = new JButton(btnText);
                btn.setFont(UIConstants.EMOJI_FONT); // Use central font

                if (isLocked) {
                    int r = (categoryColor.getRed() + 64) / 2;
                    int g = (categoryColor.getGreen() + 64) / 2;
                    int b = (categoryColor.getBlue() + 64) / 2;
                    btn.setBackground(new Color(r, g, b));
                } else {
                    btn.setBackground(categoryColor);
                }

                btn.setForeground(Color.WHITE);
                btn.setEnabled(!isLocked);

                if (!isLocked) {
                    btn.addActionListener(e -> {
                        VisualMonkeyStudio vms = new VisualMonkeyStudio(user);
                        vms.loadLevel(f);
                        vms.setVisible(true);
                        context.dispose();
                    });
                }

                row.add(btn, BorderLayout.CENTER);
                list.add(row);

                if (stars < 1) unlocked = false;
            }
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        add(scroll);

        JButton back = new JButton("BACK");
        back.addActionListener(e -> context.showMainMenu());
        JPanel backP = new JPanel();
        backP.setBackground(UIConstants.BG_COLOR);
        backP.add(back);
        add(backP);
    }
}
