package com.monkey.gui.components;

import com.monkey.auth.User;
import com.monkey.auth.UserManager;
import com.monkey.gui.LevelMenu;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.json.JSONObject;

public class LoginPanel extends JPanel {

    public LoginPanel(LevelMenu context) {
        setBackground(new Color(44, 62, 80));
        setLayout(new GridBagLayout());

        // Increased rows to fit Guest button
        JPanel card = new JPanel(new GridLayout(6, 1, 10, 10));
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
                context.setCurrentUser(user);
                context.showMainMenu();
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

        // --- NEW GUEST BUTTON ---
        JButton guestBtn = new JButton("🕵️ PLAY AS GUEST");
        guestBtn.setBackground(new Color(149, 165, 166)); // Gray
        guestBtn.setForeground(Color.WHITE);
        guestBtn.addActionListener(e -> {
            // Create a temporary user that isn't in the database
            User guest = new User("Guest", "", User.Role.PLAYER, new JSONObject());
            context.setCurrentUser(guest);
            context.showMainMenu();
            JOptionPane.showMessageDialog(context, "Playing as Guest. Progress will NOT be saved.");
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

        card.add(guestBtn); // Add Guest button at bottom

        add(card);
    }
}
