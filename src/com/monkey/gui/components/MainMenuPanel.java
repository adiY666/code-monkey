package com.monkey.gui.components;

import com.monkey.auth.User;
import com.monkey.gui.LevelMenu;
import com.monkey.gui.VisualMonkeyStudio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel {

    private static final String PATH_BASICS = "levels/basics";
    private static final String PATH_LOOPS = "levels/loops";
    private static final String PATH_VARS = "levels/vars";
    private static final String PATH_ADVANCED = "levels/advanced";
    private static final String PATH_CUSTOM = "levels/custom";
    private static final String JSON_EXT = ".json";

    public MainMenuPanel(LevelMenu context, User user) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_COLOR);

        JPanel grid = new JPanel(new GridLayout(0, 2, 20, 20));
        grid.setBackground(UIConstants.BG_COLOR);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        boolean basicsDone = isCategoryComplete(user, PATH_BASICS);
        boolean loopsDone = basicsDone && isCategoryComplete(user, PATH_LOOPS);
        boolean varsDone = loopsDone && isCategoryComplete(user, PATH_VARS);
        boolean dev = user.isDeveloper();

        addCategoryBtn(grid, context, "BASICS", UIConstants.BTN_BLUE, PATH_BASICS, true);
        addCategoryBtn(grid, context, "LOOPS", new Color(155, 89, 182), PATH_LOOPS, basicsDone || dev);
        addCategoryBtn(grid, context, "VARS", UIConstants.BTN_GREEN, PATH_VARS, loopsDone || dev);
        addCategoryBtn(grid, context, "ADVANCED", UIConstants.BTN_RED, PATH_ADVANCED, varsDone || dev);

        if (user.isAdminOrDev()) {
            addCategoryBtn(grid, context, "CUSTOM", UIConstants.BTN_ORANGE, PATH_CUSTOM, true);
        }

        add(grid, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(UIConstants.BG_COLOR);

        if (user.isAdminOrDev()) {
            JButton manageBtn = createFooterBtn("👥 MANAGE USERS", UIConstants.BTN_PURPLE);
            manageBtn.addActionListener(e -> context.showUserManagement());
            footer.add(manageBtn);
        }

        if (user.isDeveloper()) {
            JButton newMapBtn = createFooterBtn("➕ NEW MAP", UIConstants.BTN_GREEN);
            newMapBtn.addActionListener(e -> {
                context.dispose();
                VisualMonkeyStudio editor = new VisualMonkeyStudio(user);
                editor.createNewLevel();
                editor.setVisible(true);
            });
            footer.add(newMapBtn);
        }

        JButton logoutBtn = createFooterBtn("🔓 LOGOUT", new Color(192, 57, 43));
        logoutBtn.addActionListener(e -> {
            context.dispose();
            new LevelMenu(null).setVisible(true);
        });
        footer.add(logoutBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private void addCategoryBtn(JPanel p, LevelMenu ctx, String label, Color c, String path, boolean unlocked) {
        String text = unlocked ? label : "🔒 " + label;
        JButton btn = new JButton(text);

        if (unlocked) {
            btn.setBackground(c);
        } else {
            int r = (c.getRed() + 64) / 2;
            int g = (c.getGreen() + 64) / 2;
            int b = (c.getBlue() + 64) / 2;
            btn.setBackground(new Color(r, g, b));
        }

        btn.setForeground(Color.WHITE);
        btn.setFont(UIConstants.TITLE_FONT);
        btn.setEnabled(unlocked);
        if(unlocked) btn.addActionListener(e -> ctx.showLevelList(path, c));
        p.add(btn);
    }

    private JButton createFooterBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(UIConstants.EMOJI_FONT); // Use central font
        return b;
    }

    private boolean isCategoryComplete(User u, String path) {
        File folder = new File(path);
        File[] files = folder.listFiles((d, n) -> n.endsWith(JSON_EXT));
        if(files == null || files.length == 0) return false;
        for(File f : files) {
            if(u.getStars(f.getName()) < 1) return false;
        }
        return true;
    }
}
