package com.monkey.interpreter;

import com.monkey.language.CommandRegistry;
import com.monkey.language.GameCommand;
import java.util.HashSet;
import java.util.Set;

public class SyntaxChecker {

    public static String validate(String code, String currentPack, int currentLevel) {
        if (code == null || code.trim().isEmpty()) return "The code editor is empty!";

        String[] lines = code.split("\n");
        int openBraces = 0, openParens = 0;
        Set<String> declaredVars = new HashSet<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;

            for (char c : line.toCharArray()) {
                if (c == '{') openBraces++; else if (c == '}') openBraces--;
                else if (c == '(') openParens++; else if (c == ')') openParens--;
            }

            if (openBraces < 0) return "Syntax Error on line " + (i + 1) + ": Extra '}'.";
            if (openParens < 0) return "Syntax Error on line " + (i + 1) + ": Extra ')'.";

            if (line.startsWith("for") && line.contains("int ")) {
                try {
                    String varName = line.substring(line.indexOf("int "), line.indexOf(";")).replace("int ", "").split("=")[0].trim();
                    declaredVars.add(varName);
                } catch (Exception ignored) {}
            }

            if (line.startsWith("int ")) {
                String[] parts = line.split("=");
                if (parts.length >= 2) {
                    String varName = parts[0].replace("int ", "").trim();
                    String value = parts[1].replace(";", "").trim();
                    if (value.contains("\"") || value.contains("'")) {
                        return "Type Error on line " + (i + 1) + ": Cannot assign String to 'int' variable '" + varName + "'.";
                    }
                    declaredVars.add(varName);
                }
            }
            else if (line.contains("=") && !line.contains("==") && !line.contains("<=") && !line.contains(">=") && !line.contains("!=")) {
                if (!line.startsWith("for") && !line.startsWith("while")) {
                    String[] parts = line.split("[+\\-*/%]?=");
                    String varName = parts[0].trim();

                    if (!declaredVars.contains(varName)) {
                        return "Reference Error on line " + (i + 1) + ": Variable '" + varName + "' does not exist.";
                    }
                    if (parts.length >= 2) {
                        String value = parts[1].replace(";", "").trim();
                        if (value.contains("\"") || value.contains("'")) {
                            return "Type Error on line " + (i + 1) + ": Cannot assign String to 'int' variable '" + varName + "'.";
                        }
                    }
                }
            }

            if (line.contains("(") && line.contains(")")) {
                String cmdName = line.substring(0, line.indexOf("(")).trim();

                if(cmdName.contains(".")) cmdName = cmdName.substring(cmdName.indexOf(".") + 1);
                if(cmdName.contains("]")) cmdName = cmdName.substring(cmdName.indexOf("]") + 1);

                String arg = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();

                GameCommand commandRule = CommandRegistry.getCompilerRule(cmdName);

                if (commandRule != null) {
                    // --- Anti-Cheat Check! ---
                    if (!commandRule.isUnlocked(currentPack, currentLevel)) {
                        return "Locked Command on line " + (i + 1) + ": You must reach '" + commandRule.unlockPack + " Level " + commandRule.unlockLevel + "' to use '" + cmdName + "()'.";
                    }

                    String error = TypeChecker.validate(commandRule, arg, declaredVars, i + 1);
                    if (error != null) return error;
                }
            }
        }

        return (openBraces > 0 || openParens > 0) ? "Syntax Error: Missing closing brackets." : null;
    }
}
