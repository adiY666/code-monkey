package com.monkey.logic;

import java.util.Map;

public class ExpressionEvaluator {

    // Evaluates math expressions with basic order of operations
    public static int evaluateMath(String expr, Map<String, Integer> vars) {
        expr = expr.replaceAll("\\s+", ""); // remove spaces

        if (expr.isEmpty()) return 0;

        // Evaluate + and - first (we search backwards so they are calculated last in the recursive tree)
        int plusIdx = expr.lastIndexOf('+');
        if (plusIdx > 0) {
            return evaluateMath(expr.substring(0, plusIdx), vars) + evaluateMath(expr.substring(plusIdx + 1), vars);
        }

        int minusIdx = expr.lastIndexOf('-');
        if (minusIdx > 0) {
            return evaluateMath(expr.substring(0, minusIdx), vars) - evaluateMath(expr.substring(minusIdx + 1), vars);
        }

        // Evaluate *, /, //, % next
        int multIdx = expr.lastIndexOf('*');
        if (multIdx > 0) {
            return evaluateMath(expr.substring(0, multIdx), vars) * evaluateMath(expr.substring(multIdx + 1), vars);
        }

        int intDivIdx = expr.lastIndexOf("//");
        if (intDivIdx > 0) {
            int right = evaluateMath(expr.substring(intDivIdx + 2), vars);
            return right == 0 ? 0 : evaluateMath(expr.substring(0, intDivIdx), vars) / right;
        }

        int divIdx = expr.lastIndexOf('/');
        if (divIdx > 0) {
            int right = evaluateMath(expr.substring(divIdx + 1), vars);
            return right == 0 ? 0 : evaluateMath(expr.substring(0, divIdx), vars) / right;
        }

        int modIdx = expr.lastIndexOf('%');
        if (modIdx > 0) {
            int right = evaluateMath(expr.substring(modIdx + 1), vars);
            return right == 0 ? 0 : evaluateMath(expr.substring(0, modIdx), vars) % right;
        }

        return getVal(expr, vars);
    }

    public static int getVal(String s, Map<String, Integer> vars) {
        if (vars.containsKey(s)) return vars.get(s);
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    public static boolean evaluateCondition(String line, Map<String, Integer> vars) {
        try {
            int start = line.indexOf("(") + 1;
            int end = line.lastIndexOf(")");
            if (start <= 0 || end <= start) return false;

            String cond = line.substring(start, end).trim();
            if (cond.contains("<=")) {
                String[] p = cond.split("<=");
                return getVal(p[0].trim(), vars) <= getVal(p[1].trim(), vars);
            } else if (cond.contains(">=")) {
                String[] p = cond.split(">=");
                return getVal(p[0].trim(), vars) >= getVal(p[1].trim(), vars);
            } else if (cond.contains("<")) {
                String[] p = cond.split("<");
                return getVal(p[0].trim(), vars) < getVal(p[1].trim(), vars);
            } else if (cond.contains(">")) {
                String[] p = cond.split(">");
                return getVal(p[0].trim(), vars) > getVal(p[1].trim(), vars);
            } else if (cond.contains("==")) {
                String[] p = cond.split("==");
                return getVal(p[0].trim(), vars) == getVal(p[1].trim(), vars);
            } else if (cond.contains("!=")) {
                String[] p = cond.split("!=");
                return getVal(p[0].trim(), vars) != getVal(p[1].trim(), vars);
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static int parseValue(String line, Map<String, Integer> vars) {
        try {
            int start = line.indexOf("(") + 1;
            int end = line.lastIndexOf(")");
            if (start <= 0 || end <= start) return 0;

            String valStr = line.substring(start, end).trim();
            return getVal(valStr, vars);
        } catch (Exception e) { return 0; }
    }
}
