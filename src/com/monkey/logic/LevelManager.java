package com.monkey.logic;

import com.monkey.gui.AutoScrollPanel;
import com.monkey.gui.GameEnginePanel;
import com.monkey.gui.VisualMonkeyStudio;
import java.io.File;

public class LevelManager {

    // These remain public so the UI can easily access the current state
    public File currentFile = null;
    public int currentLevelLimit = 10;

    // Sub-systems
    private final LevelPlayController playController;
    private final LevelAdminController adminController;

    public LevelManager(VisualMonkeyStudio context, GameEnginePanel engine, AutoScrollPanel autoScroll) {
        LevelEditor editor = new LevelEditor(context, engine);

        // Initialize our separated modules!
        this.playController = new LevelPlayController(context, engine, autoScroll, this);
        this.adminController = new LevelAdminController(context, editor, autoScroll);
    }

    // --- GAMEPLAY DELEGATES ---
    public void loadLevel(File f) {
        playController.loadLevel(f);
    }

    public void loadNextLevel() {
        playController.loadNextLevel();
    }

    public void saveProgress(int stars) {
        playController.saveProgress(stars);
    }

    // --- EDITOR DELEGATES ---
    public void createNewLevel() {
        adminController.createNewLevel();
    }

    public void saveLevelDialog() {
        adminController.saveLevelDialog();
    }

    public void updateLevelDialog() {
        adminController.updateLevelDialog(currentFile);
    }

    public void openAdminSettings() {
        adminController.openAdminSettings();
    }
}
