package com.monkey.logic;

import com.monkey.gui.GameEnginePanel;
import com.monkey.core.IGameObject;
import java.util.ArrayList;
import java.util.List;

public class SensingEngine {

    public static List<IGameObject> getAllObjects(GameEnginePanel engine) {
        List<IGameObject> all = new ArrayList<>();
        all.addAll(engine.bananas);
        all.addAll(engine.stones);
        all.addAll(engine.rivers);
        all.addAll(engine.turtles);
        return all;
    }

    public static double getDistanceTo(GameEnginePanel engine, String type) {
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

    public static double getAngleTo(GameEnginePanel engine, String type) throws InterruptedException {
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

    public static boolean isTouched(GameEnginePanel engine, String type) {
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
        if (angleToObj < 0) angleToObj += 360;

        double monkeyAngle = engine.monkeyAngle % 360;
        if (monkeyAngle < 0) monkeyAngle += 360;

        double diff = Math.abs(monkeyAngle - angleToObj);
        if (diff > 180) diff = 360 - diff;

        if (diff >= 30) return false;

        double ax = engine.monkeyX;
        double ay = engine.monkeyY;
        double lineLenSquared = (tx - ax) * (tx - ax) + (ty - ay) * (ty - ay);

        for (com.monkey.core.GameObject stone : engine.stones) {
            if (Math.abs(stone.x - tx) < 1 && Math.abs(stone.y - ty) < 1) continue;

            double cx = stone.x;
            double cy = stone.y;

            double t = 0;
            if (lineLenSquared > 0) {
                t = ((cx - ax) * (tx - ax) + (cy - ay) * (ty - ay)) / lineLenSquared;
                t = Math.max(0, Math.min(1, t));
            }

            double projX = ax + t * (tx - ax);
            double projY = ay + t * (ty - ay);

            double distToSightLine = Math.hypot(cx - projX, cy - projY);

            if (distToSightLine <= 25.0) {
                return false;
            }
        }

        return true;
    }
}
