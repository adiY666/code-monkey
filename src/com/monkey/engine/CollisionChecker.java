package com.monkey.engine;

import com.monkey.core.GameObject;
import com.monkey.core.ITerrain;
import com.monkey.core.Turtle;
import com.monkey.gui.game.GameEnginePanel;

import java.util.ArrayList;
import java.util.List;

public class CollisionChecker {

    private static List<GameObject> getAllObjects(GameEnginePanel engine) {
        List<GameObject> allObjects = new ArrayList<>();
        allObjects.addAll(engine.stones);
        allObjects.addAll(engine.rivers);
        allObjects.addAll(engine.bananas);
        allObjects.addAll(engine.turtles);

        return allObjects;
    }

    public static boolean isSolidCollision(GameEnginePanel engine, double targetX, double targetY) {

        for (GameObject obj : getAllObjects(engine)) {
            if (obj instanceof ITerrain && ((ITerrain) obj).isSolid()) {

                if (obj.hitbox != null) {
                    if (obj.hitbox.contains(targetX, targetY)) {
                        return true;
                    }
                }
                else {
                    double radius = ((ITerrain) obj).getSize() / 2.0;
                    if (Math.hypot(obj.x - targetX, obj.y - targetY) < radius) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isDeadlyCollision(GameEnginePanel engine, double targetX, double targetY) {
        boolean overDeadlyGround = false;

        for (GameObject obj : getAllObjects(engine)) {
            if (obj instanceof ITerrain && ((ITerrain) obj).isDeadly()) {

                if (obj.hitbox != null) {
                    if (obj.hitbox.contains(targetX, targetY)) overDeadlyGround = true;
                } else {
                    double radius = ((ITerrain) obj).getSize() / 2.0;
                    if (Math.hypot(obj.x - targetX, obj.y - targetY) < radius) overDeadlyGround = true;
                }
            }

            if (overDeadlyGround) break;
        }

        if (overDeadlyGround) {
            for (Turtle turtle : engine.turtles) {
                if (Math.hypot(turtle.x - targetX, turtle.y - targetY) < 40) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public static boolean isTurtleLandCollision(GameEnginePanel engine, double targetX, double targetY) {
        boolean onWater = false;

        for (GameObject obj : getAllObjects(engine)) {
            if (obj instanceof ITerrain && ((ITerrain) obj).isDeadly()) {

                if (obj.hitbox != null) {
                    if (obj.hitbox.contains(targetX, targetY)) {
                        onWater = true;
                        break;
                    }
                } else {
                    double radius = ((ITerrain) obj).getSize() / 2.0;
                    if (Math.hypot(obj.x - targetX, obj.y - targetY) < radius) {
                        onWater = true;
                        break;
                    }
                }
            }
        }

        return !onWater;
    }
}
