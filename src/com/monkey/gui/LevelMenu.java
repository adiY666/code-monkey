package com.monkey.gui;

import com.monkey.auth.User;
import com.monkey.gui.components.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LevelMenu extends JFrame {

    private static final String TITLE = "Code Monkey - Main Menu";
    private static final Color HEADER_COLOR = new Color(52, 73, 94);

    private final JPanel contentPanel;
    private User currentUser;

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

        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        if (currentUser == null) {
            showLoginScreen();
        } else {
            showMainMenu();
        }
    }

    public void setCurrentUser(User u) { this.currentUser = u; }

    public void showLoginScreen() {
        contentPanel.removeAll();
        contentPanel.add(new LoginPanel(this));
        refresh();
    }

    public void showMainMenu() {
        contentPanel.removeAll();
        contentPanel.add(new MainMenuPanel(this, currentUser));
        refresh();
    }

    public void showLevelList(String path, Color c) {
        contentPanel.removeAll();
        contentPanel.add(new LevelListPanel(this, currentUser, path, c));
        refresh();
    }

    public void showUserManagement() {
        contentPanel.removeAll();
        contentPanel.add(new UserManagementPanel(this, currentUser));
        refresh();
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
