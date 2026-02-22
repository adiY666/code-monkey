package com.monkey.logic;

import com.monkey.gui.GameEnginePanel;
import com.monkey.core.IGameObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpressionEvaluator {

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
            return (int) getDistanceTo(engine, target);
        }

        if (s.startsWith("angleTo(")) {
            String target = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")")).trim();
            return (int) getAngleTo(engine, target);
        }

        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

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
                return not != isTouched(engine, target);
            }
            if (cond.startsWith("isSeeing(")) {
                String target = cond.substring(cond.indexOf("(") + 1, cond.lastIndexOf(")")).trim();
                return not != isSeeing(engine, target);
            }

            if (cond.contains("<=")) {
                String[] p = cond.split("<=");
                return getVal(engine, p[0].trim(), vars) <= getVal(engine, p[1].trim(), vars);
            } else if (cond.contains(">=")) {
                String[] p = cond.split(">=");
                return getVal(engine, p[0].trim(), vars) >= getVal(engine, p[1].trim(), vars);
            } else if (cond.contains("<")) {
                String[] p = cond.split("<");
                return getVal(engine, p[0].trim(), vars) < getVal(engine, p[1].trim(), vars);
            } else if (cond.contains(">")) {
                String[] p = cond.split(">");
                return getVal(engine, p[0].trim(), vars) > getVal(engine, p[1].trim(), vars);
            } else if (cond.contains("==")) {
                String[] p = cond.split("==");
                return getVal(engine, p[0].trim(), vars) == getVal(engine, p[1].trim(), vars);
            } else if (cond.contains("!=")) {
                String[] p = cond.split("!=");
                return getVal(engine, p[0].trim(), vars) != getVal(engine, p[1].trim(), vars);
            }
        } catch (Exception ignored) {}
        return false;
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

    // ==========================================
    //   DYNAMIC VISION & SENSING METHODS
    // ==========================================

    private static List<IGameObject> getAllObjects(GameEnginePanel engine) {
        List<IGameObject> all = new ArrayList<>();
        all.addAll(engine.bananas);
        all.addAll(engine.stones);
        all.addAll(engine.rivers);
        all.addAll(engine.turtles);
        return all;
    }

    private static double getDistanceTo(GameEnginePanel engine, String type) {
        type = type.replaceAll("['\"]", "").toLowerCase();
        double minDst = -1;

        for (IGameObject obj : getAllObjects(engine)) {
            if (obj.getType().equalsIgnoreCase(type)) {
                double dst = Math.hypot(obj.getX() - engine.monkeyX, obj.getY() - engine.monkeyY);
                if (minDst == -1 || dst < minDst) minDst = dst;
            }
        }
        return minDst;
    }

    private static double getAngleTo(GameEnginePanel engine, String type) throws InterruptedException {
        type = type.replaceAll("['\"]", "").toLowerCase();
        double tx = -1, ty = -1;
        double minDst = -1;

        for (IGameObject obj : getAllObjects(engine)) {
            if (obj.getType().equalsIgnoreCase(type) && inSight(engine, obj.getX(), obj.getY())) {
                double dst = Math.hypot(obj.getX() - engine.monkeyX, obj.getY() - engine.monkeyY);
                if (minDst == -1 || dst < minDst) {
                    minDst = dst;
                    tx = obj.getX(); ty = obj.getY();
                }
            }
        }

        if (tx != -1 && ty != -1) {
            double dx = tx - engine.monkeyX;
            double dy = -(ty - engine.monkeyY);
            double targetAngle = Math.toDegrees(Math.atan2(dy, dx));
            if (targetAngle < 0) targetAngle += 360;
            double monkeyAngle = engine.monkeyAngle % 360;
            if (monkeyAngle < 0) monkeyAngle += 360;
            double diff = targetAngle - monkeyAngle;
            if (diff > 180) diff -= 360;
            if (diff < -180) diff += 360;
            return diff;
        } else {
            com.monkey.animation.AnimationManager.showSightAnim(engine, false);
            return 0;
        }
    }

    private static boolean isTouched(GameEnginePanel engine, String type) {
        double dst = getDistanceTo(engine, type);
        return dst >= 0 && dst <= 35;
    }

    public static boolean isSeeing(GameEnginePanel engine, String type) {
        type = type.replaceAll("['\"]", "").toLowerCase();

        for (IGameObject obj : getAllObjects(engine)) {
            if (obj.getType().equalsIgnoreCase(type)) {
                if (inSight(engine, obj.getX(), obj.getY())) return true;
            }
        }
        return false;
    }

    private static boolean inSight(GameEnginePanel engine, double tx, double ty) {
        double dx = tx - engine.monkeyX;
        double dy = -(ty - engine.monkeyY);

        double angleToObj = Math.toDegrees(Math.atan2(dy, dx));
        if(angleToObj < 0) angleToObj += 360;

        double monkeyAngle = engine.monkeyAngle % 360;
        if(monkeyAngle < 0) monkeyAngle += 360;

        double diff = Math.abs(monkeyAngle - angleToObj);
        if(diff > 180) diff = 360 - diff;

        return diff < 30; // 60 degree vision cone
    }
}
