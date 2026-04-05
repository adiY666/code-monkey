package com.monkey.level;

import com.monkey.gui.game.GameEnginePanel;
import com.monkey.gui.editor.HistoryManager; // <--- Make sure this import matches your folder structure!

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class LevelEditor {

    private final Component parent;
    private final GameEnginePanel engine;

    // --- NEW: History & File Tracking ---
    private final HistoryManager history = new HistoryManager();
    public File currentFile = null; // Keeps track of what to save with Ctrl+S

    public LevelEditor(Component parent, GameEnginePanel engine) {
        this.parent = parent;
        this.engine = engine;

        setupKeyBindings(); // Activate shortcuts immediately
    }

    // ==========================================
    //           UNDO / REDO LOGIC
    // ==========================================

    public void takeSnapshot() {
        // Call this RIGHT BEFORE adding or removing an object on the map!
        history.saveSnapshot(engine.getLayoutAsJson());
    }

    private void undo() {
        if (history.canUndo()) {
            restoreState(history.undo(engine.getLayoutAsJson()));
        }
    }

    private void redo() {
        if (history.canRedo()) {
            restoreState(history.redo(engine.getLayoutAsJson()));
        }
    }

    private void restoreState(JSONObject json) {
        if (json == null) return;

        // Clear current map without triggering a snapshot
        engine.bananas.clear(); engine.stones.clear();
        engine.rivers.clear(); engine.turtles.clear();

        if (json.has("monkey")) {
            JSONObject m = json.getJSONObject("monkey");
            engine.monkeyX = m.getDouble("x");
            engine.monkeyY = m.getDouble("y");
            engine.monkeyAngle = m.getDouble("angle");
        }

        restoreArray(json, "bananas", "Banana");
        restoreArray(json, "stones", "Stone");
        restoreArray(json, "rivers", "River");
        restoreArray(json, "turtles", "Turtle");

        engine.repaint();
    }

    private void restoreArray(JSONObject json, String arrayName, String type) {
        if (!json.has(arrayName)) return;
        JSONArray arr = json.getJSONArray(arrayName);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            engine.addObject(type, obj.getInt("x"), obj.getInt("y"));
        }
    }

    private void setupKeyBindings() {
        // Attach shortcuts to the engine panel so they work while drawing
        InputMap im = engine.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = engine.getActionMap();

        // CTRL + Z
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "Undo");
        am.put("Undo", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { undo(); }
        });

        // CTRL + Y
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "Redo");
        am.put("Redo", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { redo(); }
        });

        // CTRL + S
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "Save");
        am.put("Save", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (currentFile != null) {
                    showUpdateDialog(currentFile);
                } else {
                    showSaveDialog();
                }
            }
        });
    }

    // ==========================================
    //           EXISTING EDITOR LOGIC
    // ==========================================

    public void clearMap() {
        takeSnapshot(); // Remember the map before clearing it!
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
            saveFile(name, cat, 1);
        }
    }

    public void showUpdateDialog(File fileToUpdate) {
        this.currentFile = fileToUpdate; // Track it for Ctrl+S

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
                JSONObject meta = LevelLoader.readMetadata(target);
                String cat = meta.optString("category", "Custom");
                int sort = meta.optInt("sort_index", 1);

                LevelLoader.saveLevel(target, engine, cat, sort);
                this.currentFile = target; // Update tracking
                JOptionPane.showMessageDialog(parent, "Updated!");
            } catch(Exception e) { e.printStackTrace(); }
        }
    }

    private void saveFile(String name, String category, int sort) {
        String path = "levels/" + category.toLowerCase().replace("variables", "vars");
        if(category.equals("Basic")) path = "levels/basics";

        new File(path).mkdirs();
        File file = new File(path, name);
        try {
            LevelLoader.saveLevel(file, engine, category, sort);
            this.currentFile = file; // Update tracking
            JOptionPane.showMessageDialog(parent, "Saved!");
        } catch(Exception e) { e.printStackTrace(); }
    }
}
