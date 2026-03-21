package com.monkey.language;

import java.awt.Color;

public class GameCommand {
    public final String commandPrefix;
    public final String requiredType;
    public final String uiLabel;
    public final String defaultCode;
    public final Color uiColor;

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

    public boolean isUnlocked(String currentPack, int currentLevel) {
        int cmdPackIdx = CommandRegistry.getPackIndex(this.unlockPack);
        int currPackIdx = CommandRegistry.getPackIndex(currentPack);

        // If in a future pack, or the exact right pack and level
        return currPackIdx > cmdPackIdx || (currPackIdx == cmdPackIdx && currentLevel >= this.unlockLevel);
    }
}