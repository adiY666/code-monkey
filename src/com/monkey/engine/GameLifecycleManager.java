package com.monkey.engine;

import com.monkey.gui.editor.EditorSidebar;
import com.monkey.gui.game.GameEnginePanel;
import com.monkey.gui.game.VisualMonkeyStudio;
import com.monkey.gui.game.WinLoseDialogs;
import com.monkey.interpreter.CodeExecutor;
import com.monkey.interpreter.SyntaxChecker;
import com.monkey.level.LevelManager;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameLifecycleManager {

    private final VisualMonkeyStudio studio;
    private final GameEnginePanel engine;
    private final EditorSidebar sidebar;
    private final LevelManager levelManager;
    private final CodeExecutor executor;

    private boolean isWinning = false;

    public GameLifecycleManager(VisualMonkeyStudio studio, GameEnginePanel engine, EditorSidebar sidebar, LevelManager levelManager) {
        this.studio = studio;
        this.engine = engine;
        this.sidebar = sidebar;
        this.levelManager = levelManager;

        this.executor = new CodeExecutor(engine, this::checkWinCondition);

        this.engine.setOnLevelComplete(this::onInstantWin);
    }

    public void executeCode(String code) {
        String currentPack = "basic";
        int currentLevelNum = 1;

        // Automatically read the folder name and file number
        if (levelManager.currentFile != null) {
            try {
                currentPack = levelManager.currentFile.getParentFile().getName();
                String name = levelManager.currentFile.getName();
                currentLevelNum = Integer.parseInt(name.replaceAll("\\D+", ""));
            } catch (Exception ignored) {}
        }

        // Pass the Folder AND the Level into the Compiler!
        String errorMessage = SyntaxChecker.validate(code, currentPack, currentLevelNum);

        if (errorMessage != null) {
            JOptionPane.showMessageDialog(studio, errorMessage, "Compile Error", JOptionPane.ERROR_MESSAGE);
            sidebar.setRunState(false);
            return;
        }

        engine.setRulerMode(0);
        isWinning = false;
        executor.execute(code);
    }

    public void stopCode() {
        executor.stop();
        engine.resetLevel();
        sidebar.setRunState(false);
    }

    private int getEffectiveLineCount() {
        String code = sidebar.codeArea.getText();
        if (code == null || code.trim().isEmpty()) return 0;

        String[] lines = code.split("\n");
        int count = 0;
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !trimmed.startsWith("//")) {
                count++;
            }
        }
        return count;
    }

    public void onInstantWin() {
        if (isWinning) return;
        isWinning = true;

        executor.stop();
        sidebar.setRunState(false);

        SwingUtilities.invokeLater(() -> {
            int limit = levelManager.currentLevelLimit;
            int linesUsed = getEffectiveLineCount();
            int stars = (limit == 0 || linesUsed <= limit) ? 3 : (linesUsed > limit * 1.5 ? 1 : 2);

            levelManager.saveProgress(stars);
            WinLoseDialogs.showWin(studio, stars);
        });
    }

    private void checkWinCondition(int linesUsed) {
        if (isWinning) return;
        sidebar.setRunState(false);

        if (engine.getBananaCount() == 0) {
            onInstantWin();
        } else {
            WinLoseDialogs.showLose(studio);
        }
    }
}
