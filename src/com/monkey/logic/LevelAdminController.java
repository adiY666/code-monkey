package com.monkey.logic;

import com.monkey.gui.AutoScrollPanel;
import com.monkey.gui.VisualMonkeyStudio;
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

    public void openAdminSettings() {
        if (checkDev()) return;
        String[] ops = {"Reorder Levels", "Delete Level"};
        int c = JOptionPane.showOptionDialog(context, "Tool:", "Admin",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, ops, ops[0]);

        if (c == 0) editor.openReorderTool();
    }

    private boolean checkDev() {
        if (context.getCurrentUser().isDeveloper()) return false;
        JOptionPane.showMessageDialog(context, "Access Denied");
        return true;
    }
}
