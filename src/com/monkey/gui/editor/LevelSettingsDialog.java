package com.monkey.gui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.monkey.gui.UIConstants;
import org.json.JSONObject;

public class LevelSettingsDialog extends JDialog {

    private final JLabel fileLblDisplay;
    private boolean changed = false;
    private File levelFile;

    public LevelSettingsDialog(JFrame parentFrame, File levelFile) {
        super(parentFrame, "Level Settings", true);
        this.levelFile = levelFile;
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
            currentContent = new String(Files.readAllBytes(this.levelFile.toPath()));
            JSONObject json = new JSONObject(currentContent);
            currentName = json.optString("name", this.levelFile.getName().replace(".json", ""));
            currentLimit = json.optInt("limit", 0);
        } catch (Exception ignored) {}

        JLabel nameLbl = new JLabel("Level Title:");
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setFont(UIConstants.NORMAL_FONT);
        JTextField nameField = new JTextField(currentName);

        JLabel fileLbl = new JLabel("Level Order:");
        fileLbl.setForeground(Color.WHITE);
        fileLbl.setFont(UIConstants.NORMAL_FONT);

        // --- REORDER TOOL INTEGRATION ---
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        filePanel.setBackground(UIConstants.BG_COLOR);
        fileLblDisplay = new JLabel(this.levelFile.getName());
        fileLblDisplay.setForeground(Color.LIGHT_GRAY);
        fileLblDisplay.setFont(UIConstants.NORMAL_FONT);

        // --- THE FIX: Small white arrow button ---
        JButton reorderBtn = new JButton("⇅");
        reorderBtn.setFont(UIConstants.EMOJI_FONT.deriveFont(16f));
        reorderBtn.setForeground(Color.WHITE);
        reorderBtn.setBackground(UIConstants.BTN_GRAY);
        reorderBtn.setFocusPainted(false);
        reorderBtn.setBorderPainted(false);
        reorderBtn.setToolTipText("Change Level Order");
        reorderBtn.addActionListener(e -> openReorderTool());

        filePanel.add(fileLblDisplay, BorderLayout.CENTER);
        filePanel.add(reorderBtn, BorderLayout.EAST);
        // ---------------------------------

        JLabel limitLbl = new JLabel("Lines to 3-Star:");
        limitLbl.setForeground(Color.WHITE);
        limitLbl.setFont(UIConstants.NORMAL_FONT);
        JTextField limitField = new JTextField(String.valueOf(currentLimit));

        panel.add(nameLbl); panel.add(nameField);
        panel.add(fileLbl); panel.add(filePanel);
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

                // Save using the CURRENT levelFile (which might have been updated by Reorder Tool)
                Files.write(this.levelFile.toPath(), json.toString(4).getBytes());

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
                this.levelFile.delete();
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

    // ==========================================
    //         REORDER TOOL INTEGRATION
    // ==========================================

    private void openReorderTool() {
        File dir = levelFile.getParentFile();
        File[] files = dir.listFiles((d, n) -> n.endsWith(".json"));
        if(files == null || files.length < 2) {
            JOptionPane.showMessageDialog(this, "Not enough levels to reorder.");
            return;
        }

        // Sort files logically (1, 2, 3, 10) instead of alphabetically (1, 10, 2)
        Arrays.sort(files, (f1, f2) -> {
            int n1 = extractNumber(f1.getName());
            int n2 = extractNumber(f2.getName());
            return Integer.compare(n1, n2);
        });

        JPanel p = new JPanel(new BorderLayout());
        DefaultListModel<File> model = new DefaultListModel<>();
        for(File f : files) model.addElement(f);

        JList<File> list = new JList<>(model);
        p.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(1, 2));
        JButton up = new JButton("▲");
        JButton down = new JButton("▼");
        btns.add(up); btns.add(down);
        p.add(btns, BorderLayout.SOUTH);

        up.addActionListener(e -> moveItem(list, model, -1));
        down.addActionListener(e -> moveItem(list, model, 1));

        // Highlight the currently edited level in the list
        list.setSelectedValue(levelFile, true);

        JOptionPane.showMessageDialog(this, p, "Reorder " + dir.getName(), JOptionPane.PLAIN_MESSAGE);
    }

    private void moveItem(JList<File> list, DefaultListModel<File> model, int dir) {
        int idx = list.getSelectedIndex();
        if(idx == -1) return;
        int newIdx = idx + dir;
        if(newIdx < 0 || newIdx >= model.size()) return;

        File f1 = model.get(idx);
        File f2 = model.get(newIdx);

        // Swap filenames on disk
        File temp = new File(f1.getParent(), "TEMP_" + System.currentTimeMillis() + ".json");
        String n1 = f1.getName();
        String n2 = f2.getName();

        f1.renameTo(temp);
        f2.renameTo(new File(f2.getParent(), n1));
        temp.renameTo(new File(f1.getParent(), n2));

        // Update List Model
        File f1New = new File(f1.getParent(), n2); // f1 now has f2's name
        File f2New = new File(f1.getParent(), n1);

        model.set(idx, f2New);
        model.set(newIdx, f1New);
        list.setSelectedIndex(newIdx);

        changed = true; // Mark as changed so LevelMenu reloads

        // IMPORTANT: If we just renamed the file we are currently editing, update the reference!
        if (f1.getAbsolutePath().equals(levelFile.getAbsolutePath())) {
            levelFile = f1New;
            fileLblDisplay.setText(levelFile.getName());
        } else if (f2.getAbsolutePath().equals(levelFile.getAbsolutePath())) {
            levelFile = f2New;
            fileLblDisplay.setText(levelFile.getName());
        }
    }

    private int extractNumber(String name) {
        try {
            String numStr = name.replaceAll("[^0-9]", "");
            return numStr.isEmpty() ? 999 : Integer.parseInt(numStr);
        } catch (Exception e) {
            return 999;
        }
    }
}
