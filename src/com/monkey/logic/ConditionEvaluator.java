package com.monkey.logic;

import com.monkey.gui.GameEnginePanel;
import java.util.Map;

public class ConditionEvaluator {

    public static boolean evaluateCondition(GameEnginePanel engine, String line, Map<String, Integer> vars) {
        try {
            int start = line.indexOf("(") + 1;
            int end = line.lastIndexOf(")");
            if (start <= 0 || end <= start) return false;

            String cond = line.substring(start, end).trim();

            boolean not = false;
            if (cond.startsWith("!")) {
                not = true;
                cond = cond.substring(1).trim();
            }

            if (cond.startsWith("isTouched(")) {
                String target = cond.substring(cond.indexOf("(") + 1, cond.lastIndexOf(")")).trim();
                return not != SensingEngine.isTouched(engine, target);
            }
            if (cond.startsWith("isSeeing(")) {
                String target = cond.substring(cond.indexOf("(") + 1, cond.lastIndexOf(")")).trim();
                return not != SensingEngine.isSeeing(engine, target);
            }

            if (cond.contains("<=")) {
                String[] p = cond.split("<=");
                return MathEvaluator.getVal(engine, p[0].trim(), vars) <= MathEvaluator.getVal(engine, p[1].trim(), vars);
            } else if (cond.contains(">=")) {
                String[] p = cond.split(">=");
                return MathEvaluator.getVal(engine, p[0].trim(), vars) >= MathEvaluator.getVal(engine, p[1].trim(), vars);
            } else if (cond.contains("<")) {
                String[] p = cond.split("<");
                return MathEvaluator.getVal(engine, p[0].trim(), vars) < MathEvaluator.getVal(engine, p[1].trim(), vars);
            } else if (cond.contains(">")) {
                String[] p = cond.split(">");
                return MathEvaluator.getVal(engine, p[0].trim(), vars) > MathEvaluator.getVal(engine, p[1].trim(), vars);
            } else if (cond.contains("==")) {
                String[] p = cond.split("==");
                return MathEvaluator.getVal(engine, p[0].trim(), vars) == MathEvaluator.getVal(engine, p[1].trim(), vars);
            } else if (cond.contains("!=")) {
                String[] p = cond.split("!=");
                return MathEvaluator.getVal(engine, p[0].trim(), vars) != MathEvaluator.getVal(engine, p[1].trim(), vars);
            }
        } catch (Exception ignored) {}
        return false;
    }
}
