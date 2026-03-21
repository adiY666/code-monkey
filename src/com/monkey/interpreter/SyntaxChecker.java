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

            String keyword = null;
            if (line.startsWith("for")) keyword = "for";
            else if (line.startsWith("while")) keyword = "while";
            else if (line.startsWith("int ")) keyword = "int";
            else if (line.startsWith("if")) keyword = "if";

            if (keyword != null) {
                GameCommand rule = CommandRegistry.getCompilerRule(keyword);
                if (rule != null && !rule.isUnlocked(currentPack, currentLevel)) {
                    return "Locked Feature on line " + (i + 1) + ": You must reach '" + rule.unlockPack + " Level " + rule.unlockLevel + "' to use '" + keyword + "'.";
                }
            }

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
                String lookupName = cmdName;

                if(cmdName.contains(".")) {
                    cmdName = cmdName.substring(cmdName.indexOf(".") + 1);
                    lookupName = "turtle." + cmdName;
                } else if(cmdName.contains("]")) {
                    cmdName = cmdName.substring(cmdName.indexOf("]") + 1);
                    lookupName = "turtle." + cmdName;
                }

                String arg = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();

                GameCommand commandRule = CommandRegistry.getCompilerRule(lookupName);
                if (commandRule == null) commandRule = CommandRegistry.getCompilerRule(cmdName);

                if (commandRule != null) {
                    if (!commandRule.isUnlocked(currentPack, currentLevel)) {
                        return "Locked Command on line " + (i + 1) + ": You must reach '" + commandRule.unlockPack + " Level " + commandRule.unlockLevel + "' to use '" + cmdName + "()'.";
                    }

                    // --- FIXED TYPE VALIDATION ---
                    String expectedType = commandRule.requiredType;
                    if (expectedType.equals("int")) {
                        // Strip out quotes to check if the inner word is left/right
                        String cleanArg = arg.replace("'", "").replace("\"", "").trim();

                        // Bypass: Allow "left" and "right" as special built-in constants
                        if (cleanArg.equals("left") || cleanArg.equals("right")) {
                            // Do nothing! Let it pass!
                        }
                        // Normal integer checks
                        else if (arg.contains("'") || arg.contains("\"")) {
                            return "Type Error on line " + (i + 1) + ": '" + cmdName + "()' requires a number or variable, not a String.";
                        } else if (arg.matches("[a-zA-Z]+") && !declaredVars.contains(arg)) {
                            return "Reference Error on line " + (i + 1) + ": Variable '" + arg + "' is used but does not exist.";
                        }
                    } else if (expectedType.equals("String")) {
                        if (!arg.contains("'") && !arg.contains("\"") && arg.matches("[a-zA-Z]+") && !declaredVars.contains(arg)) {
                            return "Reference Error on line " + (i + 1) + ": Variable '" + arg + "' does not exist. Forgot quotes?";
                        }
                    }
                }
            }
        }

        return (openBraces > 0 || openParens > 0) ? "Syntax Error: Missing closing brackets." : null;
    }
}
