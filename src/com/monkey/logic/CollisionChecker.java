package com.monkey.logic;

import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import com.monkey.gui.GameEnginePanel;

public class CollisionChecker {

    // Hitbox sizes for the 50x50 tiles
    private static final double OBSTACLE_RADIUS = 50.0;
    private static final double RIVER_RADIUS = 50.0;
    private static final double TURTLE_RADIUS = 50.0;

    // Returns TRUE if the target position hits a stone
    public static boolean isStoneCollision(GameEnginePanel engine, double targetX, double targetY) {
        for (GameObject stone : engine.stones) {
            if (Math.hypot(stone.x - targetX, stone.y - targetY) < OBSTACLE_RADIUS) {
                return true;
            }
        }
        return false;
    }

    // Returns TRUE if the monkey is drowning (in water without a turtle)
    public static boolean isWaterCollision(GameEnginePanel engine, double targetX, double targetY) {
        boolean overWater = false;
        for (GameObject river : engine.rivers) {
            if (Math.hypot(river.x - targetX, river.y - targetY) < RIVER_RADIUS) {
                overWater = true;
                break;
            }
        }

        if (overWater) {
            boolean onTurtle = false;
            for (Turtle turtle : engine.turtles) {
                if (Math.hypot(turtle.x - targetX, turtle.y - targetY) < TURTLE_RADIUS) {
                    onTurtle = true;
                    break;
                }
            }
            return !onTurtle; // If over water but NOT on a turtle, it's a collision!
        }
        return false;
    }

    // Returns TRUE if the turtle walks onto land
    public static boolean isTurtleLandCollision(GameEnginePanel engine, double targetX, double targetY) {
        boolean onWater = false;
        for (GameObject river : engine.rivers) {
            if (Math.hypot(river.x - targetX, river.y - targetY) < RIVER_RADIUS) {
                onWater = true;
                break;
            }
        }
        return !onWater; // If NOT on water, it's a collision!
    }
}
