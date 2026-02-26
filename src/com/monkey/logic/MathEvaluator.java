package com.monkey.logic;

import com.monkey.gui.GameEnginePanel;
import java.util.Map;

public class MathEvaluator {

    public static int evaluateMath(GameEnginePanel engine, String expr, Map<String, Integer> vars) throws InterruptedException {
        expr = expr.replaceAll("\\s+", "");
        if (expr.isEmpty()) return 0;

        int plusIdx = expr.lastIndexOf('+');
        if (plusIdx > 0) return evaluateMath(engine, expr.substring(0, plusIdx), vars) + evaluateMath(engine, expr.substring(plusIdx + 1), vars);

        int minusIdx = expr.lastIndexOf('-');
        if (minusIdx > 0) return evaluateMath(engine, expr.substring(0, minusIdx), vars) - evaluateMath(engine, expr.substring(minusIdx + 1), vars);

        int multIdx = expr.lastIndexOf('*');
        if (multIdx > 0) return evaluateMath(engine, expr.substring(0, multIdx), vars) * evaluateMath(engine, expr.substring(multIdx + 1), vars);

        int intDivIdx = expr.lastIndexOf("//");
        if (intDivIdx > 0) {
            int right = evaluateMath(engine, expr.substring(intDivIdx + 2), vars);
            return right == 0 ? 0 : evaluateMath(engine, expr.substring(0, intDivIdx), vars) / right;
        }

        int divIdx = expr.lastIndexOf('/');
        if (divIdx > 0) {
            int right = evaluateMath(engine, expr.substring(divIdx + 1), vars);
            return right == 0 ? 0 : evaluateMath(engine, expr.substring(0, divIdx), vars) / right;
        }

        int modIdx = expr.lastIndexOf('%');
        if (modIdx > 0) {
            int right = evaluateMath(engine, expr.substring(modIdx + 1), vars);
            return right == 0 ? 0 : evaluateMath(engine, expr.substring(0, modIdx), vars) % right;
        }

        return getVal(engine, expr, vars);
    }

    public static int getVal(GameEnginePanel engine, String s, Map<String, Integer> vars) throws InterruptedException {
        if (vars.containsKey(s)) return vars.get(s);

        if (s.startsWith("distanceTo(")) {
            String target = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")")).trim();
            return (int) SensingEngine.getDistanceTo(engine, target);
        }

        if (s.startsWith("angleTo(")) {
            String target = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")")).trim();
            return (int) SensingEngine.getAngleTo(engine, target);
        }

        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    public static int parseValue(GameEnginePanel engine, String line, Map<String, Integer> vars) {
        try {
            int start = line.indexOf("(") + 1;
            int end = line.lastIndexOf(")");
            if (start <= 0 || end <= start) return 0;

            String valStr = line.substring(start, end).trim();
            return evaluateMath(engine, valStr, vars);
        } catch (Exception e) { return 0; }
    }
}
