package com.monkey.gui.components;

import com.monkey.auth.User;
import com.monkey.gui.LevelMenu;
import com.monkey.gui.VisualMonkeyStudio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class LevelListPanel extends JPanel {

    private final LevelMenu menu;
    private final User currentUser;
    private final String folderPath;
    private final Color folderColor;
    private final JPanel listContainer;

    public LevelListPanel(LevelMenu menu, User user, String path, Color headerColor) {
        this.menu = menu;
        this.currentUser = user;
        this.folderPath = path;
        this.folderColor = (headerColor != null) ? headerColor : UIConstants.HEADER_COLOR;

        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Select a Level", SwingConstants.CENTER);
        title.setFont(UIConstants.TITLE_FONT);
        title.setForeground(this.folderColor);
        title.setOpaque(true);
        title.setBackground(UIConstants.BG_COLOR);
        add(title, BorderLayout.NORTH);

        // List Container
        listContainer = new JPanel(new GridLayout(0, 1, 10, 10));
        listContainer.setBackground(UIConstants.BG_COLOR);

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        scroll.setBackground(UIConstants.BG_COLOR);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Back Button
        JButton backBtn = new JButton("Back to Menu");
        backBtn.setBackground(UIConstants.BTN_RED);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(UIConstants.NORMAL_FONT);
        backBtn.addActionListener(e -> menu.showMainMenu());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(UIConstants.BG_COLOR);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // Initial Load
        refreshList();
    }

    private void refreshList() {
        listContainer.removeAll();

        File dir = new File(folderPath);
        File[] fileArray = dir.exists() ? dir.listFiles((d, name) -> name.endsWith(".json")) : new File[0];
        List<File> levels = fileArray != null ? Arrays.asList(fileArray) : List.of();

        if (levels.isEmpty()) {
            JLabel noLevels = new JLabel("No levels found in " + folderPath, SwingConstants.CENTER);
            noLevels.setFont(UIConstants.NORMAL_FONT);
            noLevels.setForeground(Color.WHITE);
            listContainer.add(noLevels);
            listContainer.revalidate();
            listContainer.repaint();
            return;
        }

        // Sort levels numerically
        levels.sort((f1, f2) -> {
            int n1 = extractNumber(f1.getName());
            int n2 = extractNumber(f2.getName());
            return Integer.compare(n1, n2);
        });

        // Progression Locking Logic
        boolean previousBeaten = true;
        boolean isDev = "DEVELOPER".equalsIgnoreCase(currentUser.getRole().name());

        for (File file : levels) {
            int stars = currentUser.getStars(file.getName());
            boolean isLocked = !isDev && !previousBeaten;

            listContainer.add(createLevelRow(file, stars, isLocked));

            previousBeaten = (stars > 0);
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel createLevelRow(File file, int stars, boolean isLocked) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        String name = file.getName().replace(".json", "");
        JButton playBtn = new JButton();
        playBtn.setFont(UIConstants.EMOJI_FONT);
        playBtn.setPreferredSize(new Dimension(0, 50));
        playBtn.setFocusPainted(false);

        if (isLocked) {
            // --- LOCKED LEVEL ---
            playBtn.setText("🔒  " + name);

            // Create a grayer, darker tone of the folder's color
            Color darkTone = this.folderColor.darker().darker();
            int r = (darkTone.getRed() + 40) / 2;
            int g = (darkTone.getGreen() + 40) / 2;
            int b = (darkTone.getBlue() + 40) / 2;

            playBtn.setBackground(new Color(r, g, b));
            playBtn.setForeground(Color.LIGHT_GRAY);
            playBtn.setEnabled(false);
        } else {
            // --- UNLOCKED LEVEL ---
            String starStr = "⭐".repeat(stars);
            playBtn.setText(name + "  " + starStr);
            playBtn.setEnabled(true);

            // Unlocked levels get the pure folder color!
            playBtn.setBackground(this.folderColor);
            playBtn.setForeground(Color.WHITE);

            playBtn.addActionListener(e -> {
                menu.dispose();
                new VisualMonkeyStudio(currentUser, file).setVisible(true);
            });
        }

        row.add(playBtn, BorderLayout.CENTER);

        // Settings Button (DEVELOPER ONLY)
        if ("DEVELOPER".equalsIgnoreCase(currentUser.getRole().name())) {
            JButton settingsBtn = new JButton("⚙️");
            settingsBtn.setBackground(UIConstants.BTN_GRAY);
            settingsBtn.setForeground(Color.WHITE);
            settingsBtn.setFont(UIConstants.EMOJI_FONT);
            settingsBtn.setPreferredSize(new Dimension(50, 50));
            settingsBtn.setFocusPainted(false);

            settingsBtn.addActionListener(e -> {
                LevelSettingsDialog dialog = new LevelSettingsDialog(menu, file);
                dialog.setVisible(true);

                if (dialog.isChanged()) refreshList();
            });

            row.add(settingsBtn, BorderLayout.EAST);
        }

        return row;
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
