package com.monkey.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Files;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.json.JSONObject;

public class LevelSettingsDialog extends JDialog {

    private boolean changed = false;

    public LevelSettingsDialog(JFrame parentFrame, File levelFile) {
        super(parentFrame, "Level Settings", true);
        setSize(400, 250);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.BG_COLOR);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(UIConstants.BG_COLOR);

        // Fetch current data directly from the JSON file
        String currentName = "";
        int currentLimit = 0;
        String currentContent = "{}";
        try {
            currentContent = new String(Files.readAllBytes(levelFile.toPath()));
            JSONObject json = new JSONObject(currentContent);
            currentName = json.optString("name", levelFile.getName().replace(".json", ""));
            currentLimit = json.optInt("limit", 0);
        } catch (Exception ignored) {}

        JLabel nameLbl = new JLabel("Level Title:");
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setFont(UIConstants.NORMAL_FONT);
        JTextField nameField = new JTextField(currentName);

        JLabel fileLbl = new JLabel("File Name (Order):");
        fileLbl.setForeground(Color.WHITE);
        fileLbl.setFont(UIConstants.NORMAL_FONT);
        JTextField fileField = new JTextField(levelFile.getName());

        JLabel limitLbl = new JLabel("Lines to 3-Star:");
        limitLbl.setForeground(Color.WHITE);
        limitLbl.setFont(UIConstants.NORMAL_FONT);
        JTextField limitField = new JTextField(String.valueOf(currentLimit));

        panel.add(nameLbl); panel.add(nameField);
        panel.add(fileLbl); panel.add(fileField);
        panel.add(limitLbl); panel.add(limitField);
        add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(UIConstants.BG_COLOR);

        JButton saveBtn = createBtn("SAVE", UIConstants.BTN_GREEN);
        final String finalContent = currentContent;

        saveBtn.addActionListener(e -> {
            try {
                JSONObject json = new JSONObject(finalContent);
                json.put("name", nameField.getText().trim());
                json.put("limit", Integer.parseInt(limitField.getText().trim()));

                File targetFile = levelFile;
                String newFileName = fileField.getText().trim();
                if (!newFileName.endsWith(".json")) newFileName += ".json";

                if (!newFileName.equals(levelFile.getName())) {
                    targetFile = new File(levelFile.getParentFile(), newFileName);
                }

                Files.write(targetFile.toPath(), json.toString(4).getBytes());

                if (!targetFile.getAbsolutePath().equals(levelFile.getAbsolutePath())) {
                    levelFile.delete();
                }

                changed = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
            }
        });

        JButton delBtn = createBtn("DELETE", UIConstants.BTN_RED);
        delBtn.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to completely delete this level?",
                    "Warning", JOptionPane.YES_NO_OPTION);

            if (res == JOptionPane.YES_OPTION) {
                levelFile.delete();
                changed = true;
                dispose();
            }
        });

        JButton cancelBtn = createBtn("CANCEL", UIConstants.BTN_GRAY);
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(delBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    public boolean isChanged() {
        return changed;
    }

    private JButton createBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(UIConstants.NORMAL_FONT);
        b.setFocusPainted(false);
        return b;
    }
}
