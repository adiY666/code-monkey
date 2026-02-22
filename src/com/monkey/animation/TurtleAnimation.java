package com.monkey.animation;

import com.monkey.core.Turtle;
import com.monkey.gui.GameEnginePanel;
import com.monkey.logic.CollisionChecker;
import java.util.concurrent.CountDownLatch;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

public class TurtleAnimation {

    public static void smoothStep(GameEnginePanel engine, Turtle turtle, double distance, int frames) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        double stepDist = distance / frames;

        double rad = Math.toRadians(turtle.angle);
        double dx = stepDist * Math.cos(rad);
        double dy = -stepDist * Math.sin(rad);

        boolean monkeyOnBoard = Math.hypot(engine.monkeyX - turtle.x, engine.monkeyY - turtle.y) < 30;
        final boolean[] hitDeadlyObstacle = {false};

        SwingUtilities.invokeLater(() -> new Timer(20, new java.awt.event.ActionListener() {
            int count = 0;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    double nextX = turtle.x + dx;
                    double nextY = turtle.y + dy;

                    // 1. Turtle going on land fails the level
                    if (CollisionChecker.isTurtleLandCollision(engine, nextX, nextY)) {
                        throw new RuntimeException("Turtle walked onto land!");
                    }

                    // 2. Check Monkey Collisions
                    if (monkeyOnBoard) {
                        double nextMonkeyX = engine.monkeyX + dx;
                        double nextMonkeyY = engine.monkeyY + dy;

                        // --- CHANGED: Monkey hits stone while riding? Stop moving gently. ---
                        if (CollisionChecker.isStoneCollision(engine, nextMonkeyX, nextMonkeyY)) {
                            ((Timer) e.getSource()).stop();
                            latch.countDown();
                            return;
                        }

                        // Monkey falls in water (shouldn't happen on a turtle, but just in case)
                        if (CollisionChecker.isWaterCollision(engine, nextMonkeyX, nextMonkeyY)) {
                            throw new RuntimeException("Monkey fell in the water!");
                        }

                        engine.monkeyX = nextMonkeyX;
                        engine.monkeyY = nextMonkeyY;
                    }

                    // Safely apply move
                    turtle.x = nextX;
                    turtle.y = nextY;

                    engine.checkCollisions();
                    engine.repaint();

                    count++;
                    if (count >= frames) {
                        ((Timer) e.getSource()).stop();
                        latch.countDown();
                    }
                } catch (RuntimeException ex) {
                    // Deadly obstacle hit (water/land)! Stop the animation and signal failure.
                    hitDeadlyObstacle[0] = true;
                    ((Timer) e.getSource()).stop();
                    latch.countDown();
                }
            }
        }).start());

        latch.await();
        if (hitDeadlyObstacle[0]) {
            throw new RuntimeException("Deadly obstacle hit during turtle movement!");
        }
    }

    public static void smoothTurn(GameEnginePanel engine, Turtle turtle, double angle, int frames) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        double stepAngle = angle / frames;

        boolean monkeyOnBoard = Math.hypot(engine.monkeyX - turtle.x, engine.monkeyY - turtle.y) < 30;

        SwingUtilities.invokeLater(() -> new Timer(20, new java.awt.event.ActionListener() {
            int count = 0;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                turtle.angle += stepAngle;

                if (monkeyOnBoard) {
                    engine.monkeyAngle += stepAngle;
                }

                engine.repaint();
                count++;
                if (count >= frames) {
                    ((Timer) e.getSource()).stop();
                    latch.countDown();
                }
            }
        }).start());

        latch.await();
    }
}
