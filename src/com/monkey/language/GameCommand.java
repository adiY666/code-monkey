package com.monkey.language;

import java.awt.Color;

public class GameCommand {
    public final String commandPrefix;
    public final String requiredType;
    public final String uiLabel;
    public final String defaultCode;
    public final Color uiColor;

    // --- CHANGED: Now uses Folder + Number ---
    public final String unlockPack;
    public final int unlockLevel;

    public GameCommand(String commandPrefix, String requiredType, String uiLabel, String defaultCode, Color uiColor, String unlockPack, int unlockLevel) {
        this.commandPrefix = commandPrefix;
        this.requiredType = requiredType;
        this.uiLabel = uiLabel;
        this.defaultCode = defaultCode;
        this.uiColor = uiColor;
        this.unlockPack = unlockPack;
        this.unlockLevel = unlockLevel;
    }

    // --- NEW: Smart check to see if the command is unlocked ---
    public boolean isUnlocked(String currentPack, int currentLevel) {
        int cmdPackIdx = CommandRegistry.getPackIndex(this.unlockPack);
        int currPackIdx = CommandRegistry.getPackIndex(currentPack);

        // If they are in a later folder, it's definitely unlocked!
        if (currPackIdx > cmdPackIdx) return true;
        // If they are in the exact same folder, check the level number!
        if (currPackIdx == cmdPackIdx && currentLevel >= this.unlockLevel) return true;

        return false;
    }
}
