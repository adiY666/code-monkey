package com.monkey.level;

import com.monkey.gui.game.GameEnginePanel;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;
import javax.swing.*;

import org.json.JSONObject;

public class LevelEditor {

    private final Component parent;
    private final GameEnginePanel engine;

    public LevelEditor(Component parent, GameEnginePanel engine) {
        this.parent = parent;
        this.engine = engine;
    }

    public void clearMap() {
        engine.bananas.clear(); engine.stones.clear();
        engine.rivers.clear(); engine.turtles.clear();
        engine.monkeyX = 350; engine.monkeyY = 300; engine.monkeyAngle = 0;
        engine.levelLimit = 0;
        engine.repaint();
    }

    public void showSaveDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField nameField = new JTextField();
        String[] categories = {"Basic", "Loops", "Variables", "Advanced", "Custom"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        JSpinner limitSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));

        panel.add(new JLabel("Level Name:")); panel.add(nameField);
        panel.add(new JLabel("Category:")); panel.add(categoryBox);
        panel.add(new JLabel("Lines:")); panel.add(limitSpinner);

        if(JOptionPane.showConfirmDialog(parent, panel, "Save New Level", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if(name.isEmpty()) return;
            if(!name.endsWith(".json")) name += ".json";

            String cat = (String) categoryBox.getSelectedItem();
            engine.levelLimit = (int) limitSpinner.getValue();

            assert cat != null;
            saveFile(name, cat, 1); // Default sort index 1
        }
    }

    public void showUpdateDialog(File currentFile) {
        List<File> allLevels = LevelLoader.getAllLevels();
        if(allLevels.isEmpty()) return;

        JComboBox<String> picker = new JComboBox<>();
        for(File f : allLevels) picker.addItem(f.getName() + " [" + f.getParentFile().getName() + "]");

        // Auto-select current file
        if (currentFile != null) {
            for(int i=0; i<picker.getItemCount(); i++) {
                if(picker.getItemAt(i).startsWith(currentFile.getName())) {
                    picker.setSelectedIndex(i);
                    break;
                }
            }
        }

        if(JOptionPane.showConfirmDialog(parent, picker, "Select Level to Overwrite", JOptionPane.OK_CANCEL_OPTION) == 0) {
            File target = allLevels.get(picker.getSelectedIndex());
            try {
                // Preserve metadata
                JSONObject meta = LevelLoader.readMetadata(target);
                String cat = meta.optString("category", "Custom");
                int sort = meta.optInt("sort_index", 1);

                LevelLoader.saveLevel(target, engine, cat, sort);
                JOptionPane.showMessageDialog(parent, "Updated!");
            } catch(Exception e) { e.printStackTrace(); }
        }
    }

    private void saveFile(String name, String category, int sort) {
        String path = "levels/" + category.toLowerCase().replace("variables", "vars");
        if(category.equals("Basic")) path = "levels/basics"; // Fix mapping

        new File(path).mkdirs();
        File file = new File(path, name);
        try {
            LevelLoader.saveLevel(file, engine, category, sort);
            JOptionPane.showMessageDialog(parent, "Saved!");
        } catch(Exception e) { e.printStackTrace(); }
    }
}
