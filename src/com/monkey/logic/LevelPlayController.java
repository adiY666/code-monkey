package com.monkey.logic;

import com.monkey.gui.AutoScrollPanel;
import com.monkey.gui.GameEnginePanel;
import com.monkey.gui.VisualMonkeyStudio;
import java.io.File;
import javax.swing.JOptionPane;

public class LevelPlayController {

    private final VisualMonkeyStudio context;
    private final GameEnginePanel engine;
    private final AutoScrollPanel autoScroll;
    private final LevelManager manager; // Reference to update the Hub's variables

    public LevelPlayController(VisualMonkeyStudio context, GameEnginePanel engine, AutoScrollPanel autoScroll, LevelManager manager) {
        this.context = context;
        this.engine = engine;
        this.autoScroll = autoScroll;
        this.manager = manager;
    }

    public void loadLevel(File f) {
        if (f == null) return;
        manager.currentFile = f;

        try {
            LevelLoader.loadLevel(f, engine);
            manager.currentLevelLimit = engine.levelLimit;
            autoScroll.updateMapSize();
            engine.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(context, "Error loading level.");
        }
    }

    public void loadNextLevel() {
        File next = LevelProgression.getNextLevel(manager.currentFile);
        if (next != null) {
            loadLevel(next);
        } else {
            JOptionPane.showMessageDialog(context, "Pack Complete!");
            context.returnToMenu();
        }
    }

    public void saveProgress(int stars) {
        LevelProgression.saveStars(context.getCurrentUser(), manager.currentFile, stars);
    }
}
