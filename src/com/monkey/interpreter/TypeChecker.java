package com.monkey.interpreter;

import com.monkey.language.GameCommand;

import java.util.Set;

public class TypeChecker {

    public static String validate(GameCommand commandRule, String arg, Set<String> declaredVars, int lineNum) {
        String expectedType = commandRule.requiredType;

        // 1. INT Commands (e.g. step, turn)
        if (expectedType.equals("int")) {
            if (arg.contains("'") || arg.contains("\"")) {
                return "Type Error on line " + lineNum + ": '" + commandRule.commandPrefix + "()' requires a number or numeric variable, not a String.";
            }
            if (arg.matches("[a-zA-Z]+") && !declaredVars.contains(arg) && !arg.equals("left") && !arg.equals("right")) {
                return "Reference Error on line " + lineNum + ": Variable '" + arg + "' is used in " + commandRule.commandPrefix + "() but does not exist.";
            }
        }
        // 2. STRING Commands (e.g. distanceTo, isSeeing)
        else if (expectedType.equals("String")) {
            if (!arg.contains("'") && !arg.contains("\"")) {
                if (arg.matches("[a-zA-Z]+") && !declaredVars.contains(arg)) {
                    return "Reference Error on line " + lineNum + ": Variable '" + arg + "' does not exist. (Did you forget quotes around an object name?)";
                }
            }
        }

        return null; // OK - No errors!
    }
}
