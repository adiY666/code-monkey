package com.monkey.level;

import com.monkey.gui.editor.AutoScrollPanel;
import com.monkey.gui.game.VisualMonkeyStudio;

import java.io.File;
import javax.swing.JOptionPane;

public class LevelAdminController {

    private final VisualMonkeyStudio context;
    private final AutoScrollPanel autoScroll;
    private final LevelEditor editor;

    public LevelAdminController(VisualMonkeyStudio context, LevelEditor editor, AutoScrollPanel autoScroll) {
        this.context = context;
        this.editor = editor;
        this.autoScroll = autoScroll;
    }

    public void createNewLevel() {
        if (checkDev()) return;
        editor.clearMap();
        autoScroll.updateMapSize();
    }

    public void saveLevelDialog() {
        if (checkDev()) return;
        editor.showSaveDialog();
    }

    public void updateLevelDialog(File currentFile) {
        if (checkDev()) return;
        editor.showUpdateDialog(currentFile);
    }

    private boolean checkDev() {
        if (context.getCurrentUser().isDeveloper()) return false;
        JOptionPane.showMessageDialog(context, "Access Denied");
        return true;
    }
}
