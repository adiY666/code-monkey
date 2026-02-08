package com.monkey.logic;

import com.monkey.auth.User;
import com.monkey.auth.UserManager;
import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import com.monkey.gui.AutoScrollPanel;
import com.monkey.gui.GameEnginePanel;
import com.monkey.gui.VisualMonkeyStudio;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class LevelManager {

    private static final String JSON_EXT = ".json";

    private static final String DIR_BASICS = "levels/basics";
    private static final String DIR_LOOPS = "levels/loops";
    private static final String DIR_VARS = "levels/vars";
    private static final String DIR_ADVANCED = "levels/advanced";
    private static final String DIR_CUSTOM = "levels/custom";

    private static final String KEY_LAYOUT = "layout";
    private static final String KEY_START_X = "start_x";
    private static final String KEY_START_Y = "start_y";
    private static final String KEY_START_ANGLE = "start_angle";
    private static final String KEY_TARGET_LINES = "target_lines";
    private static final String KEY_SORT_INDEX = "sort_index";
    private static final String KEY_CATEGORY = "category";

    private final VisualMonkeyStudio context;
    private final GameEnginePanel engine;
    private final AutoScrollPanel autoScroll;

    public File currentFile = null;
    public int currentLevelLimit = 10;

    public LevelManager(VisualMonkeyStudio context, GameEnginePanel engine, AutoScrollPanel autoScroll) {
        this.context = context;
        this.engine = engine;
        this.autoScroll = autoScroll;
    }

    public void createNewLevel() {
        if(!context.getCurrentUser().isDeveloper()) return;

        engine.bananas.clear(); engine.stones.clear();
        engine.rivers.clear(); engine.turtles.clear();
        engine.monkeyX = 350; engine.monkeyY = 300; engine.monkeyAngle = 0;
        engine.levelLimit = 0;

        autoScroll.updateMapSize();
        engine.repaint();
    }

    public void loadLevel(File f) {
        if(f == null) return;
        this.currentFile = f;
        try {
            String content = new String(Files.readAllBytes(Paths.get(f.getPath())));
            JSONObject json = new JSONObject(content);
            engine.bananas.clear(); engine.stones.clear();
            engine.rivers.clear(); engine.turtles.clear();

            engine.monkeyX = json.optDouble(KEY_START_X, 350);
            engine.monkeyY = json.optDouble(KEY_START_Y, 300);
            engine.monkeyAngle = json.optDouble(KEY_START_ANGLE, 0);
            this.currentLevelLimit = json.optInt(KEY_TARGET_LINES, 5);
            engine.levelLimit = this.currentLevelLimit;

            JSONObject layout = json.getJSONObject(KEY_LAYOUT);
            parseObj(layout.getJSONArray("bananas"), engine.bananas);
            parseObj(layout.getJSONArray("stones"), engine.stones);
            parseObj(layout.getJSONArray("rivers"), engine.rivers);
            JSONArray tArr = layout.getJSONArray("turtles");
            for(int i = 0; i < tArr.length(); i++) {
                JSONArray t = tArr.getJSONArray(i);
                engine.turtles.add(new Turtle(t.getDouble(0), t.getDouble(1), t.getInt(2), t.getDouble(3)));
            }

            autoScroll.updateMapSize();
            engine.repaint();

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(context, "Error loading level.");
        }
    }

    public void loadNextLevel() {
        if(currentFile == null) return;
        File folder = currentFile.getParentFile();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(JSON_EXT));
        if(files != null) {
            java.util.Arrays.sort(files);
            for(int i = 0; i < files.length; i++) {
                if(files[i].equals(currentFile)) {
                    if(i + 1 < files.length) {
                        loadLevel(files[i + 1]);
                    } else {
                        JOptionPane.showMessageDialog(context, "Pack Complete!");
                        context.returnToMenu();
                    }
                    return;
                }
            }
        }
    }

    public void saveProgress(int stars) {
        if(currentFile == null) return;
        User user = context.getCurrentUser();
        if(user != null) {
            int currentBest = user.getStars(currentFile.getName());
            if(stars > currentBest) {
                user.setStars(currentFile.getName(), stars);
                UserManager.getInstance().saveUsers();
            }
        }
    }

    public void saveLevelDialog() {
        if(!context.getCurrentUser().isDeveloper()) return;

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField nameField = new JTextField();
        String[] categories = {"Basic", "Loops", "Variables", "Advanced", "Custom"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        JSpinner lineLimitSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        JSpinner sortSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));

        panel.add(new JLabel("Level Name:")); panel.add(nameField);
        panel.add(new JLabel("Category:")); panel.add(categoryBox);
        panel.add(new JLabel("Target Lines:")); panel.add(lineLimitSpinner);
        panel.add(new JLabel("Sort Index:")); panel.add(sortSpinner);

        if(JOptionPane.showConfirmDialog(context, panel, "Save New Level", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if(name.isEmpty()) return;
            saveJson(name, (String)categoryBox.getSelectedItem(), (int)lineLimitSpinner.getValue(), (int)sortSpinner.getValue());
        }
    }

    // --- NEW: UPDATE EXISTING LEVEL DIALOG ---
    public void updateLevelDialog() {
        if(!context.getCurrentUser().isDeveloper()) return;

        List<File> allLevels = new ArrayList<>();
        scanFolder(new File(DIR_BASICS), allLevels);
        scanFolder(new File(DIR_LOOPS), allLevels);
        scanFolder(new File(DIR_VARS), allLevels);
        scanFolder(new File(DIR_ADVANCED), allLevels);
        scanFolder(new File(DIR_CUSTOM), allLevels);

        if(allLevels.isEmpty()) {
            JOptionPane.showMessageDialog(context, "No levels found to update!");
            return;
        }

        JComboBox<String> levelPicker = new JComboBox<>();
        for(File f : allLevels) levelPicker.addItem(f.getName() + " [" + f.getParentFile().getName() + "]");

        // Pre-select current file if loaded
        if (currentFile != null) {
            for(int i=0; i<levelPicker.getItemCount(); i++) {
                if (levelPicker.getItemAt(i).startsWith(currentFile.getName())) {
                    levelPicker.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Select Level to OVERWRITE with current layout:"));
        panel.add(levelPicker);

        if(JOptionPane.showConfirmDialog(context, panel, "Update Level", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            File targetFile = allLevels.get(levelPicker.getSelectedIndex());
            overwriteLevel(targetFile);
        }
    }

    private void overwriteLevel(File file) {
        try {
            // Read existing data to preserve Sort Order and Category
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
            JSONObject existing = new JSONObject(content);

            String category = existing.optString(KEY_CATEGORY, "Custom");
            int sortIndex = existing.optInt(KEY_SORT_INDEX, 1);
            int targetLines = engine.levelLimit; // Use CURRENT editor setting, not old one

            // Re-use logic: Overwrite the file at its path
            // We use FileWriter on the specific file object directly
            try(FileWriter fw = new FileWriter(file)) {
                JSONObject root = new JSONObject();
                root.put(KEY_LAYOUT, engine.getLayoutAsJson());
                root.put(KEY_START_X, engine.monkeyX);
                root.put(KEY_START_Y, engine.monkeyY);
                root.put(KEY_START_ANGLE, engine.monkeyAngle);
                root.put(KEY_TARGET_LINES, targetLines);
                root.put(KEY_SORT_INDEX, sortIndex);
                root.put(KEY_CATEGORY, category);

                fw.write(root.toString(4));
                JOptionPane.showMessageDialog(context, "Level Updated Successfully!");
            }
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(context, "Error updating level.");
        }
    }

    private void saveJson(String name, String category, int limit, int sortIndex) {
        String folderPath = resolveCategoryPath(category);
        new File(folderPath).mkdirs();
        String filename = folderPath + File.separator + name + JSON_EXT;

        try(FileWriter fw = new FileWriter(filename)) {
            JSONObject root = new JSONObject();
            root.put(KEY_LAYOUT, engine.getLayoutAsJson());
            root.put(KEY_START_X, engine.monkeyX);
            root.put(KEY_START_Y, engine.monkeyY);
            root.put(KEY_START_ANGLE, engine.monkeyAngle);
            root.put(KEY_TARGET_LINES, limit);
            root.put(KEY_SORT_INDEX, sortIndex);
            root.put(KEY_CATEGORY, category);
            fw.write(root.toString(4));
            JOptionPane.showMessageDialog(context, "Saved!");
        } catch(Exception e) { e.printStackTrace(); }
    }

    public void openAdminSettings() {
        if(!context.getCurrentUser().isDeveloper()) {
            JOptionPane.showMessageDialog(context, "Only Developers can edit levels!");
            return;
        }

        List<File> allLevels = new ArrayList<>();
        scanFolder(new File(DIR_BASICS), allLevels);
        scanFolder(new File(DIR_LOOPS), allLevels);
        scanFolder(new File(DIR_VARS), allLevels);
        scanFolder(new File(DIR_ADVANCED), allLevels);
        scanFolder(new File(DIR_CUSTOM), allLevels);

        if(allLevels.isEmpty()) {
            JOptionPane.showMessageDialog(context, "No levels found!");
            return;
        }

        JComboBox<String> levelPicker = new JComboBox<>();
        for(File f : allLevels) levelPicker.addItem(f.getName() + " [" + f.getParentFile().getName() + "]");

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Select Level:")); panel.add(levelPicker);

        if(JOptionPane.showConfirmDialog(context, panel, "Settings", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            editSpecificLevel(allLevels.get(levelPicker.getSelectedIndex()));
        }
    }

    private void editSpecificLevel(File file) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
            JSONObject json = new JSONObject(content);
            String oldName = file.getName().replace(JSON_EXT, "");
            int oldLimit = json.optInt(KEY_TARGET_LINES, 5);
            int oldSort = json.optInt(KEY_SORT_INDEX, 1);
            String oldFolder = json.optString("category", "Custom");

            JTextField nameField = new JTextField(oldName);
            String[] folders = {"Basic", "Loops", "Variables", "Advanced", "Custom"};
            JComboBox<String> folderBox = new JComboBox<>(folders);
            folderBox.setSelectedItem(oldFolder);
            JSpinner limitSpinner = new JSpinner(new SpinnerNumberModel(oldLimit, 1, 100, 1));
            JSpinner sortSpinner = new JSpinner(new SpinnerNumberModel(oldSort, 0, 999, 1));

            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            panel.add(new JLabel("Name:")); panel.add(nameField);
            panel.add(new JLabel("Category:")); panel.add(folderBox);
            panel.add(new JLabel("Lines:")); panel.add(limitSpinner);
            panel.add(new JLabel("Sort:")); panel.add(sortSpinner);

            Object[] options = {"Save", "Delete", "Cancel"};
            int action = JOptionPane.showOptionDialog(context, panel, "Edit " + oldName,
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if(action == 1) {
                if(file.delete()) JOptionPane.showMessageDialog(context, "Deleted.");
            } else if(action == 0) {
                String newName = nameField.getText().trim();
                String newCat = (String) folderBox.getSelectedItem();
                int newLimit = (int) limitSpinner.getValue();
                int newSort = (int) sortSpinner.getValue();

                json.put(KEY_TARGET_LINES, newLimit);
                json.put(KEY_CATEGORY, newCat);
                json.put(KEY_SORT_INDEX, newSort);

                String folderPath = resolveCategoryPath(newCat);
                new File(folderPath).mkdirs();
                File newFile = new File(folderPath, newName + JSON_EXT);
                try(FileWriter fw = new FileWriter(newFile)) { fw.write(json.toString(4)); }
                if(!newFile.getAbsolutePath().equals(file.getAbsolutePath())) file.delete();
                JOptionPane.showMessageDialog(context, "Updated!");
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void scanFolder(File dir, List<File> list) {
        if(dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(JSON_EXT));
            if(files != null) for(File f : files) list.add(f);
        }
    }

    private String resolveCategoryPath(String category) {
        return switch(category) {
            case "Basic" -> DIR_BASICS;
            case "Loops" -> DIR_LOOPS;
            case "Variables" -> DIR_VARS;
            case "Advanced" -> DIR_ADVANCED;
            default -> DIR_CUSTOM;
        };
    }

    private void parseObj(JSONArray arr, List<GameObject> list) {
        for(int i = 0; i < arr.length(); i++)
            list.add(new GameObject(arr.getJSONArray(i).getDouble(0), arr.getJSONArray(i).getDouble(1)));
    }
}
