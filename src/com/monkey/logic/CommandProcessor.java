package com.monkey.logic;

import com.monkey.animation.AnimationManager;
import com.monkey.core.Turtle;
import com.monkey.gui.GameEnginePanel;
import java.util.Map;

public class CommandProcessor {

    private final GameEnginePanel engine;
    private static final double MOVEMENT_SPEED = 5.0;

    public CommandProcessor(GameEnginePanel engine) {
        this.engine = engine;
    }

    public GameEnginePanel getEngine() {
        return engine;
    }

    public boolean process(String line, Map<String, Integer> vars) throws InterruptedException {
        if (line.endsWith(";")) line = line.substring(0, line.length() - 1);

        if (line.startsWith("step(")) {
            int dist = ExpressionEvaluator.parseValue(engine, line, vars);
            int frames = (int) Math.max(1, Math.abs(dist) / MOVEMENT_SPEED);
            AnimationManager.smoothStep(engine, dist, frames);
            return true;
        }
        else if (line.startsWith("turn(")) {
            double angle = getTurnAngle(line, vars);
            AnimationManager.smoothTurn(engine, angle, 15);
            return true;
        }
        // --- NEW: showSight command! ---
        else if (line.startsWith("showSight(")) {
            String target = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
            boolean seesIt = ExpressionEvaluator.isSeeing(engine, target);
            AnimationManager.showSightAnim(engine, seesIt);
            return true;
        }
        else if (line.startsWith("turtles[") || line.startsWith("turtle.")) {
            return handleTurtle(line, vars);
        }

        return false;
    }

    private boolean handleTurtle(String line, Map<String, Integer> vars) throws InterruptedException {
        int index = 0;
        String cmd = line;

        try {
            if (line.startsWith("turtles[")) {
                index = Integer.parseInt(line.substring(8, line.indexOf("]")));
                cmd = line.substring(line.indexOf("]") + 2);
            } else if (line.startsWith("turtle.")) {
                cmd = line.substring(7);
            }

            if (index >= engine.turtles.size()) return false;
            Turtle t = engine.turtles.get(index);

            if (cmd.startsWith("step(")) {
                int dist = ExpressionEvaluator.parseValue(engine, cmd, vars);
                int frames = (int) Math.max(1, Math.abs(dist) / MOVEMENT_SPEED);
                com.monkey.animation.TurtleAnimation.smoothStep(engine, t, dist, frames);
            } else if (cmd.startsWith("turn(")) {
                double angle = getTurnAngle(cmd, vars);
                com.monkey.animation.TurtleAnimation.smoothTurn(engine, t, angle, 15);
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private double getTurnAngle(String line, Map<String, Integer> vars) {
        if (line.contains("'left'") || line.contains("\"left\"")) return 90;
        else if (line.contains("'right'") || line.contains("\"right\"")) return -90;
        else return ExpressionEvaluator.parseValue(engine, line, vars);
    }
}
