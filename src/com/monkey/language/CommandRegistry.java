package com.monkey.language;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private static final Map<String, GameCommand> compilerRules = new HashMap<>();

    // --- NEW: Define the order of your folders! ---
    public static final String[] PACK_ORDER = {"basic", "loops", "vars", "turtles"};

    public static int getPackIndex(String pack) {
        if (pack == null) return -1;
        for (int i = 0; i < PACK_ORDER.length; i++) {
            if (PACK_ORDER[i].equalsIgnoreCase(pack)) return i;
        }
        return 999; // If it's a custom or unknown pack, unlock everything!
    }

    private static final Color BLUE = new Color(52, 152, 219);
    private static final Color PURPLE = new Color(155, 89, 182);
    private static final Color GREEN = new Color(46, 204, 113);
    private static final Color TEAL = new Color(0, 150, 136);
    private static final Color ORANGE = new Color(230, 126, 34);

    // --- COMMANDS: Now using "folder", level ---
    public static final GameCommand STEP_FWD = registerRule(new GameCommand("step", "int", "⬆ STEP(50)", "step(50);\n", BLUE, "basic", 1));
    public static final GameCommand STEP_BCK = new GameCommand("step", "int", "⬇ BACK(-50)", "step(-50);\n", BLUE, "basic", 1);
    public static final GameCommand TURN_L = registerRule(new GameCommand("turn", "int", "↰ LEFT", "turn('left');\n", BLUE, "basic", 2));
    public static final GameCommand TURN_R = new GameCommand("turn", "int", "↱ RIGHT", "turn('right');\n", BLUE, "basic", 2);

    // Unlocks later in the 'basic' folder
    public static final GameCommand DIST_TO = registerRule(new GameCommand("distanceTo", "String", "📏 DISTANCE TO", "distanceTo()", GREEN, "vars", 3));
    public static final GameCommand ANG_TO = registerRule(new GameCommand("angleTo", "String", "📐 ANGLE TO", "angleTo()", GREEN, "vars", 5));
    public static final GameCommand IS_TOUCH = registerRule(new GameCommand("isTouched", "String", "👆 TOUCHING", "isTouched()", GREEN, "vars", 3));
    public static final GameCommand IS_SEE = registerRule(new GameCommand("isSeeing", "String", "👁 SIGHT", "isSeeing()", GREEN, "vars", 5));

    // Unlocks inside the 'loops' folder
    public static final GameCommand LOOP_FOR = new GameCommand("for", "none", "🔄 REPEAT 3", "for(int i = 0; i < 3; i++){\n\n}\n", PURPLE, "loops", 1);
    public static final GameCommand LOOP_WHILE = new GameCommand("while", "none", "❓ WHILE", "while(x < 300){\n\n}\n", PURPLE, "loops", 1);

    // Unlocks inside the 'vars' folder
    public static final GameCommand VAR_INT = new GameCommand("int", "none", "int x = 0", "int x = 0;\n", GREEN, "vars", 1);
    public static final GameCommand VAR_STEP = new GameCommand("step", "int", "step(x)", "step(x);\n", GREEN, "vars", 1);

    // Unlocks inside the 'turtles' folder
    public static final GameCommand T_STEP_FWD = new GameCommand("step", "int", "🐢 STEP(50)", "turtles[0].step(50);\n", TEAL, "basic", 3);
    public static final GameCommand T_STEP_BCK = new GameCommand("step", "int", "🐢 STEP(-50)", "turtles[0].step(-50);\n", TEAL, "basic", 3);
    public static final GameCommand T_TURN_L = new GameCommand("turn", "int", "🐢 ↰ LEFT", "turtles[0].turn('left');\n", TEAL, "basic", 3);
    public static final GameCommand T_TURN_R = new GameCommand("turn", "int", "🐢 ↱ RIGHT", "turtles[0].turn('right');\n", TEAL, "basic", 3);

    // Objects available from the start
    public static final GameCommand OBJ_BANANA = new GameCommand("banana", "none", "🍌 BANANA", "'banana'", ORANGE, "vars", 3);
    public static final GameCommand OBJ_STONE = new GameCommand("stone", "none", "🪨 STONE", "'stone'", ORANGE, "vars", 3);
    public static final GameCommand OBJ_TURTLE = new GameCommand("turtle", "none", "🐢 TURTLE", "'turtle'", ORANGE, "vars", 3);
    public static final GameCommand OBJ_RIVER = new GameCommand("river", "none", "🌊 RIVER", "'river'", ORANGE, "vars", 3);

    private static GameCommand registerRule(GameCommand cmd) {
        compilerRules.put(cmd.commandPrefix, cmd);
        return cmd;
    }

    public static GameCommand getCompilerRule(String commandName) {
        return compilerRules.get(commandName);
    }
}
