package com.monkey.gui;

import com.monkey.auth.User;
import com.monkey.auth.UserManager;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

public class LevelMenu extends JFrame {

    private static final String TITLE = "Code Monkey - Main Menu";
    private static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.BOLD, 14);
    private static final Color BG_COLOR = new Color(44, 62, 80);
    private static final Color HEADER_COLOR = new Color(52, 73, 94);

    private static final String PATH_BASICS = "levels/basics";
    private static final String PATH_LOOPS = "levels/loops";
    private static final String PATH_VARS = "levels/vars";
    private static final String PATH_ADVANCED = "levels/advanced";
    private static final String PATH_CUSTOM = "levels/custom";
    private static final String JSON_EXT = ".json";

    private final JPanel contentPanel;
    private User currentUser;

    public LevelMenu() {
        this(null);
    }

    public LevelMenu(User user) {
        this.currentUser = user;
        setTitle(TITLE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("CODE MONKEY STUDIO", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(HEADER_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setLayout(new GridBagLayout());
        add(contentPanel, BorderLayout.CENTER);

        if (currentUser == null) {
            showLoginScreen();
        } else {
            showMainMenu();
        }
    }

    private void showLoginScreen() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridLayout(5, 1, 10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(20, 40, 20, 40)));
        card.setBackground(new Color(52, 73, 94));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        ActionListener loginAction = e -> {
            String u = userField.getText();
            String p = new String(passField.getPassword());
            User user = UserManager.getInstance().login(u, p);
            if(user != null) {
                this.currentUser = user;
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        };
        userField.addActionListener(loginAction);
        passField.addActionListener(loginAction);

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(46, 204, 113));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(loginAction);

        JButton regBtn = new JButton("REGISTER");
        regBtn.setBackground(new Color(52, 152, 219));
        regBtn.setForeground(Color.WHITE);
        regBtn.addActionListener(e -> {
            String u = userField.getText();
            String p = new String(passField.getPassword());
            if(UserManager.getInstance().register(u, p)) {
                JOptionPane.showMessageDialog(this, "Registered! Please Login.");
            } else {
                JOptionPane.showMessageDialog(this, "Error: User exists or empty fields.");
            }
        });

        JLabel l1 = new JLabel("Username:"); l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel("Password:"); l2.setForeground(Color.WHITE);

        card.add(l1); card.add(userField);
        card.add(l2); card.add(passField);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(loginBtn);
        btnPanel.add(regBtn);
        card.add(btnPanel);

        contentPanel.add(card);
        refresh();
    }

    private void showMainMenu() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(0, 2, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        boolean basicsDone = isCategoryComplete(PATH_BASICS);
        boolean loopsDone = basicsDone && isCategoryComplete(PATH_LOOPS);
        boolean varsDone = loopsDone && isCategoryComplete(PATH_VARS);

        boolean dev = currentUser.isDeveloper();

        addCategoryBtn("BASICS", new Color(52, 152, 219), PATH_BASICS, true);
        addCategoryBtn("LOOPS", new Color(155, 89, 182), PATH_LOOPS, basicsDone || dev);
        addCategoryBtn("VARS", new Color(46, 204, 113), PATH_VARS, loopsDone || dev);
        addCategoryBtn("ADVANCED", new Color(231, 76, 60), PATH_ADVANCED, varsDone || dev);

        if (currentUser.isAdminOrDev()) {
            addCategoryBtn("CUSTOM", new Color(230, 126, 34), PATH_CUSTOM, true);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_COLOR);

        if (currentUser.isAdminOrDev()) {
            JButton manageBtn = new JButton("👥 MANAGE USERS");
            manageBtn.setBackground(new Color(142, 68, 173));
            manageBtn.setForeground(Color.WHITE);
            manageBtn.setFont(EMOJI_FONT);
            manageBtn.addActionListener(e -> showUserManagement());
            footer.add(manageBtn);
        }

        if (currentUser.isDeveloper()) {
            JButton newMapBtn = new JButton("➕ NEW MAP");
            newMapBtn.setBackground(new Color(46, 204, 113));
            newMapBtn.setForeground(Color.WHITE);
            newMapBtn.setFont(EMOJI_FONT);
            newMapBtn.addActionListener(e -> {
                VisualMonkeyStudio editor = new VisualMonkeyStudio(currentUser);
                editor.createNewLevel();
                editor.setVisible(true);
                this.dispose();
            });
            footer.add(newMapBtn);
        }

        JButton logoutBtn = new JButton("🔓 LOGOUT");
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(EMOJI_FONT);
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LevelMenu(null).setVisible(true);
        });
        footer.add(logoutBtn);

        BorderLayout layout = (BorderLayout) getContentPane().getLayout();
        Component south = layout.getLayoutComponent(BorderLayout.SOUTH);
        if(south != null) remove(south);
        add(footer, BorderLayout.SOUTH);

        refresh();
    }

    private void showUserManagement() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);

        for (User u : UserManager.getInstance().getAllUsers()) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            row.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY));

            String pass = u.toJSON().getString("password");
            JLabel nameLbl = new JLabel(u.getUsername() + " [Pass: " + pass + "]");
            nameLbl.setPreferredSize(new Dimension(250, 30));
            nameLbl.setFont(new Font("Arial", Font.BOLD, 14));

            User.Role[] roles = User.Role.values();
            JComboBox<User.Role> roleBox = new JComboBox<>(roles);
            roleBox.setSelectedItem(u.getRole());

            boolean isSuper = u.getUsername().equalsIgnoreCase(UserManager.SUPER_USER);
            if(isSuper) {
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
                JOptionPane.showMessageDialog(this, "Reset Done.");
            });

            JButton giveBtn = new JButton("GIVE ⭐");
            giveBtn.setBackground(new Color(46, 204, 113));
            giveBtn.setForeground(Color.WHITE);
            giveBtn.addActionListener(e -> openGiveDialog(u));

            JButton deleteBtn = new JButton("DELETE");
            deleteBtn.setBackground(Color.RED);
            deleteBtn.setForeground(Color.WHITE);

            boolean canDelete = false;
            if(!isSuper && u != currentUser) {
                if(currentUser.isDeveloper()) canDelete = true;
                else if(currentUser.isAdmin() && !u.isAdmin() && !u.isDeveloper()) canDelete = true;
            }
            deleteBtn.setEnabled(canDelete);
            deleteBtn.addActionListener(e -> {
                int conf = JOptionPane.showConfirmDialog(this, "Delete " + u.getUsername() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if(conf == JOptionPane.YES_OPTION) {
                    UserManager.getInstance().deleteUser(u);
                    showUserManagement();
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

        contentPanel.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton backBtn = new JButton("BACK");
        backBtn.addActionListener(e -> showMainMenu());
        contentPanel.add(backBtn, BorderLayout.SOUTH);

        refresh();
    }

    // --- UPDATED GIVE DIALOG (UNLOCK PREVIOUS) ---
    private void openGiveDialog(User u) {
        // 1. Build a Master List of all sequential levels in order
        List<File> allLevels = new ArrayList<>();
        addFolderLevels(new File(PATH_BASICS), allLevels);
        addFolderLevels(new File(PATH_LOOPS), allLevels);
        addFolderLevels(new File(PATH_VARS), allLevels);
        addFolderLevels(new File(PATH_ADVANCED), allLevels);

        if(allLevels.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No levels found in system!");
            return;
        }

        // 2. Create display names
        String[] levelNames = new String[allLevels.size()];
        for(int i=0; i<allLevels.size(); i++) levelNames[i] = allLevels.get(i).getName();

        JComboBox<String> levelBox = new JComboBox<>(levelNames);
        String[] starsOpts = {"1 Star", "2 Stars", "3 Stars"};
        JComboBox<String> starBox = new JComboBox<>(starsOpts);
        starBox.setSelectedIndex(2); // Default 3 stars

        JPanel p = new JPanel(new GridLayout(0, 1));
        p.add(new JLabel("Select target level (will unlock everything before it):"));
        p.add(levelBox);
        p.add(new JLabel("Give Stars:"));
        p.add(starBox);

        int res = JOptionPane.showConfirmDialog(this, p, "Grant Progress to " + u.getUsername(), JOptionPane.OK_CANCEL_OPTION);

        if(res == JOptionPane.OK_OPTION) {
            int selectedIndex = levelBox.getSelectedIndex();
            int stars = starBox.getSelectedIndex() + 1;

            // 3. Loop from 0 to selectedIndex and grant stars
            int count = 0;
            for(int i = 0; i <= selectedIndex; i++) {
                String lvlName = allLevels.get(i).getName();
                // Only upgrade, don't downgrade if they have progress (optional, but requested usually implies overwriting)
                // Here we simply set it, as 'Give' implies admin override.
                if (u.getStars(lvlName) < stars) {
                    u.setStars(lvlName, stars);
                    count++;
                }
            }
            UserManager.getInstance().saveUsers();
            JOptionPane.showMessageDialog(this, "Updated " + count + " levels up to " + allLevels.get(selectedIndex).getName());
        }
    }

    private void addFolderLevels(File dir, List<File> list) {
        if(!dir.exists()) return;
        File[] files = dir.listFiles((d, n) -> n.endsWith(JSON_EXT));
        if(files != null) {
            Arrays.sort(files);
            for(File f : files) list.add(f);
        }
    }

    private void addCategoryBtn(String label, Color c, String path, boolean unlocked) {
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
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setEnabled(unlocked);
        if(unlocked) btn.addActionListener(e -> showLevelList(path, c));
        contentPanel.add(btn);
    }

    private void showLevelList(String path, Color c) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        File folder = new File(path);
        File[] files = folder.listFiles((d, n) -> n.endsWith(JSON_EXT));

        JPanel list = new JPanel(new GridLayout(0, 1, 10, 10));
        list.setBackground(BG_COLOR);

        if (files != null) {
            Arrays.sort(files);
            boolean unlocked = true;

            for (File f : files) {
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);

                int stars = currentUser.getStars(f.getName());
                String sStr = stars > 0 ? " (" + "⭐".repeat(stars) + ")" : "";

                boolean isLocked = !unlocked && !currentUser.isDeveloper();

                String btnText = isLocked ? "🔒 LOCKED" : "📄 " + f.getName().replace(".json", "") + sStr;

                JButton btn = new JButton(btnText);
                btn.setFont(EMOJI_FONT);

                if (isLocked) {
                    int r = (c.getRed() + 64) / 2;
                    int g = (c.getGreen() + 64) / 2;
                    int b = (c.getBlue() + 64) / 2;
                    btn.setBackground(new Color(r, g, b));
                } else {
                    btn.setBackground(c);
                }

                btn.setForeground(Color.WHITE);
                btn.setEnabled(!isLocked);

                if (!isLocked) {
                    btn.addActionListener(e -> {
                        VisualMonkeyStudio vms = new VisualMonkeyStudio(currentUser);
                        vms.loadLevel(f);
                        vms.setVisible(true);
                        this.dispose();
                    });
                }

                row.add(btn, BorderLayout.CENTER);

                if(currentUser.isDeveloper()) {
                    JButton settingsBtn = new JButton("⚙");
                    settingsBtn.setFont(EMOJI_FONT);
                    settingsBtn.setBackground(new Color(149, 165, 166));
                    settingsBtn.setForeground(Color.WHITE);
                    settingsBtn.setPreferredSize(new Dimension(50, 30));
                    settingsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "To edit, open map in Editor Mode."));
                    row.add(settingsBtn, BorderLayout.EAST);
                }

                list.add(row);

                if (stars < 1) unlocked = false;
            }
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        contentPanel.add(scroll);

        JButton back = new JButton("BACK");
        back.addActionListener(e -> showMainMenu());
        JPanel backP = new JPanel(); backP.setBackground(BG_COLOR); backP.add(back);
        contentPanel.add(backP);

        refresh();
    }

    private boolean isCategoryComplete(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles((d, n) -> n.endsWith(JSON_EXT));
        if(files == null || files.length == 0) return false;
        for(File f : files) {
            if(currentUser.getStars(f.getName()) < 1) return false;
        }
        return true;
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
