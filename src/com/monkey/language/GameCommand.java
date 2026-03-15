package com.monkey.language;

import java.awt.Color;

public class GameCommand {
    public final String commandPrefix;
    public final String requiredType;
    public final String uiLabel;
    public final String defaultCode;
    public final Color uiColor;

    // Unlock Requirements
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

    // Smart check to see if the command should be available
    public boolean isUnlocked(String currentPack, int currentLevel) {
        int cmdPackIdx = CommandRegistry.getPackIndex(this.unlockPack);
        int currPackIdx = CommandRegistry.getPackIndex(currentPack);

        // If in a future pack, or the exact right pack and level
        if (currPackIdx > cmdPackIdx) return true;
        if (currPackIdx == cmdPackIdx && currentLevel >= this.unlockLevel) return true;

        return false;
    }
}
