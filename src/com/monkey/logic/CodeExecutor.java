package com.monkey.logic;

import com.monkey.animation.AnimationManager;
import com.monkey.core.Turtle;
import com.monkey.gui.GameEnginePanel;
import java.util.function.IntConsumer;
import javax.swing.SwingUtilities;

public class CodeExecutor {

    private final GameEnginePanel engine;
    private final IntConsumer onComplete;
    private volatile boolean isRunning = false; // "volatile" ensures threads see the change immediately

    // --- CONFIGURATION ---
    // Higher number = Faster Monkey.
    // 5.0 means it moves 5 pixels every 20ms (approx 250 pixels/second)
    private static final double MOVEMENT_SPEED = 5.0;

    public CodeExecutor(GameEnginePanel engine, IntConsumer onComplete) {
        this.engine = engine;
        this.onComplete = onComplete;
    }

    public void execute(String code) {
        if (isRunning) return;
        isRunning = true;

        engine.resetLevel(); // Reset before running

        new Thread(() -> {
            String[] lines = code.split("\n");
            int count = 0;

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;

                try {
                    // Stop if the user clicked "Stop"
                    if (!isRunning) break;

                    boolean success = processLine(line);
                    if (success) {
                        count++;
                        // Small pause between commands so they don't blend together
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.err.println("Error processing line: " + line + " -> " + e.getMessage());
                }
            }

            isRunning = false;

            // Send final result back to UI thread
            final int resultCount = count;
            SwingUtilities.invokeLater(() -> onComplete.accept(resultCount));

        }).start();
    }

    public void stop() {
        isRunning = false;
    }

    private boolean processLine(String line) throws InterruptedException {
        if (line.endsWith(";")) line = line.substring(0, line.length() - 1);

        // --- 1. CONSTANT SPEED MOVE ---
        if (line.startsWith("step(")) {
            int dist = parseValue(line);

            // Calculate frames based on distance so speed is constant
            // Math.max(1, ...) ensures we always have at least 1 frame of animation
            int frames = (int) Math.max(1, Math.abs(dist) / MOVEMENT_SPEED);

            AnimationManager.smoothStep(engine, dist, frames);
            return true;
        }

        // --- 2. SMOOTH TURN ---
        else if (line.startsWith("turn(")) {
            double angle = getTurnAngle(line);
            // Rotations can stay fixed (15 frames) or you can apply the same logic here
            AnimationManager.smoothTurn(engine, angle, 15);
            return true;
        }

        // --- 3. TURTLE COMMANDS ---
        else if (line.startsWith("turtles[") || line.startsWith("turtle.")) {
            return handleTurtle(line);
        }

        return false;
    }

    private boolean handleTurtle(String line) {
        int index = 0;
        String cmd = line;

        try {
            if(line.startsWith("turtles[")) {
                index = Integer.parseInt(line.substring(8, line.indexOf("]")));
                cmd = line.substring(line.indexOf("]") + 2);
            } else if(line.startsWith("turtle.")) {
                cmd = line.substring(7);
            }

            if(index >= engine.turtles.size()) return false;
            Turtle t = engine.turtles.get(index);

            if(cmd.startsWith("step(")) {
                double dist = parseValue(cmd);
                t.step(dist);
            } else if(cmd.startsWith("turn(")) {
                double angle = getTurnAngle(cmd);
                t.turn(angle);
            }
            engine.repaint();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private double getTurnAngle(String line) {
        if (line.contains("'left'") || line.contains("\"left\"")) return 90;
        else if (line.contains("'right'") || line.contains("\"right\"")) return -90;
        else return parseValue(line);
    }

    private int parseValue(String line) {
        try {
            int start = line.indexOf("(") + 1;
            int end = line.lastIndexOf(")");
            return Integer.parseInt(line.substring(start, end).trim());
        } catch (Exception e) { return 0; }
    }
}
