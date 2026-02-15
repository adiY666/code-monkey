package com.monkey.logic;

import com.monkey.gui.AutoScrollPanel;
import com.monkey.gui.GameEnginePanel;
import com.monkey.gui.VisualMonkeyStudio;
import java.io.File;
import javax.swing.JOptionPane;

public class LevelManager {

    private final VisualMonkeyStudio context;
    private final GameEnginePanel engine;
    private final AutoScrollPanel autoScroll;

    // Sub-modules
    private final LevelEditor editor;

    public File currentFile = null;
    public int currentLevelLimit = 10;

    public LevelManager(VisualMonkeyStudio context, GameEnginePanel engine, AutoScrollPanel autoScroll) {
        this.context = context;
        this.engine = engine;
        this.autoScroll = autoScroll;
        this.editor = new LevelEditor(context, engine);
    }

    // --- GAMEPLAY DELEGATES ---

    public void loadLevel(File f) {
        if(f == null) return;
        this.currentFile = f;
        try {
            LevelLoader.loadLevel(f, engine);
            this.currentLevelLimit = engine.levelLimit;
            autoScroll.updateMapSize();
            engine.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(context, "Error loading level.");
        }
    }

    public void loadNextLevel() {
        File next = LevelProgression.getNextLevel(currentFile);
        if(next != null) {
            loadLevel(next);
        } else {
            JOptionPane.showMessageDialog(context, "Pack Complete!");
            context.returnToMenu();
        }
    }

    public void saveProgress(int stars) {
        LevelProgression.saveStars(context.getCurrentUser(), currentFile, stars);
    }

    // --- EDITOR DELEGATES ---

    public void createNewLevel() {
        if(!checkDev()) return;
        editor.clearMap();
        autoScroll.updateMapSize();
    }

    public void saveLevelDialog() {
        if(!checkDev()) return;
        editor.showSaveDialog();
    }

    public void updateLevelDialog() {
        if(!checkDev()) return;
        editor.showUpdateDialog(currentFile);
    }

    public void openAdminSettings() {
        if(!checkDev()) return;
        String[] ops = {"Reorder Levels", "Delete Level"}; // Simplified
        int c = JOptionPane.showOptionDialog(context, "Tool:", "Admin", 0, 1, null, ops, ops[0]);
        if(c == 0) editor.openReorderTool();
        // Delete tool logic can stay here or move to Editor
    }

    private boolean checkDev() {
        if(context.getCurrentUser().isDeveloper()) return true;
        JOptionPane.showMessageDialog(context, "Access Denied");
        return false;
    }
}
