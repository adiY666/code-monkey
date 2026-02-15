package com.monkey.gui.components;

import com.monkey.auth.User;
import com.monkey.auth.UserManager;
import com.monkey.gui.LevelMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class UserManagementPanel extends JPanel {

    private final LevelMenu context;
    private final User currentUser;

    public UserManagementPanel(LevelMenu context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        refreshList();
    }

    private void refreshList() {
        removeAll();

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);

        for (User u : UserManager.getInstance().getAllUsers()) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

            String pass = u.toJSON().getString("password");
            JLabel nameLbl = new JLabel(u.getUsername() + " [Pass: " + pass + "]");
            nameLbl.setPreferredSize(new Dimension(250, 30));
            nameLbl.setFont(new Font("Arial", Font.BOLD, 14));

            User.Role[] roles = User.Role.values();
            JComboBox<User.Role> roleBox = new JComboBox<>(roles);
            roleBox.setSelectedItem(u.getRole());

            boolean isSuper = u.getUsername().equalsIgnoreCase(UserManager.SUPER_USER);
            if (isSuper) {
                roleBox.setEnabled(false);
            } else {
                roleBox.addActionListener(e -> {
                    u.setRole((User.Role) roleBox.getSelectedItem());
                    UserManager.getInstance().saveUsers();
                });
            }

            JButton resetBtn = new JButton("RESET");
            resetBtn.setBackground(new Color(243, 156, 18));
            resetBtn.setForeground(Color.WHITE);
            resetBtn.addActionListener(e -> {
                u.resetProgress();
                UserManager.getInstance().saveUsers();
                JOptionPane.showMessageDialog(context, "Reset Done.");
            });

            JButton giveBtn = new JButton("GIVE ⭐");
            giveBtn.setBackground(new Color(46, 204, 113));
            giveBtn.setForeground(Color.WHITE);
            giveBtn.addActionListener(e -> openGiveDialog(u));

            JButton deleteBtn = new JButton("DELETE");
            deleteBtn.setBackground(Color.RED);
            deleteBtn.setForeground(Color.WHITE);

            boolean canDelete = false;
            if (!isSuper && u != currentUser) {
                if (currentUser.isDeveloper()) canDelete = true;
                else if (currentUser.isAdmin() && !u.isAdmin() && !u.isDeveloper()) canDelete = true;
            }
            deleteBtn.setEnabled(canDelete);
            deleteBtn.addActionListener(e -> {
                int conf = JOptionPane.showConfirmDialog(context, "Delete " + u.getUsername() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    UserManager.getInstance().deleteUser(u);
                    refreshList(); // Reload this panel
                    revalidate();
                    repaint();
                }
            });

            row.add(nameLbl);
            row.add(new JLabel("Role:"));
            row.add(roleBox);
            row.add(resetBtn);
            row.add(giveBtn);
            row.add(deleteBtn);
            list.add(row);
        }

        add(new JScrollPane(list), BorderLayout.CENTER);

        JButton backBtn = new JButton("BACK");
        backBtn.addActionListener(e -> context.showMainMenu());
        add(backBtn, BorderLayout.SOUTH);
    }

    private void openGiveDialog(User u) {
        List<File> allLevels = new ArrayList<>();
        addFolderLevels(new File("levels/basics"), allLevels);
        addFolderLevels(new File("levels/loops"), allLevels);
        addFolderLevels(new File("levels/vars"), allLevels);
        addFolderLevels(new File("levels/advanced"), allLevels);

        if (allLevels.isEmpty()) {
            JOptionPane.showMessageDialog(context, "No levels found!");
            return;
        }

        String[] levelNames = new String[allLevels.size()];
        for (int i = 0; i < allLevels.size(); i++) levelNames[i] = allLevels.get(i).getName();

        JComboBox<String> levelBox = new JComboBox<>(levelNames);
        String[] starsOpts = {"1 Star", "2 Stars", "3 Stars"};
        JComboBox<String> starBox = new JComboBox<>(starsOpts);
        starBox.setSelectedIndex(2);

        JPanel p = new JPanel(new GridLayout(0, 1));
        p.add(new JLabel("Select level (unlocks all previous):"));
        p.add(levelBox);
        p.add(new JLabel("Stars:"));
        p.add(starBox);

        int res = JOptionPane.showConfirmDialog(context, p, "Grant Progress to " + u.getUsername(), JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            int selectedIndex = levelBox.getSelectedIndex();
            int stars = starBox.getSelectedIndex() + 1;
            int count = 0;
            for (int i = 0; i <= selectedIndex; i++) {
                String lvlName = allLevels.get(i).getName();
                if (u.getStars(lvlName) < stars) {
                    u.setStars(lvlName, stars);
                    count++;
                }
            }
            UserManager.getInstance().saveUsers();
            JOptionPane.showMessageDialog(context, "Unlocked " + count + " levels.");
        }
    }

    private void addFolderLevels(File dir, List<File> list) {
        if (!dir.exists()) return;
        File[] files = dir.listFiles((d, n) -> n.endsWith(".json"));
        if (files != null) {
            Arrays.sort(files);
            for (File f : files) list.add(f);
        }
    }
}
