package com.monkey.animation;

import com.monkey.core.Turtle;
import com.monkey.gui.GameEnginePanel;
import java.util.concurrent.CountDownLatch;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

public class TurtleAnimation {

    public static void smoothStep(GameEnginePanel engine, Turtle turtle, double distance, int frames) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        double stepDist = distance / frames;

        // Calculate X and Y direction based on the turtle's rotation
        double rad = Math.toRadians(turtle.angle);
        double dx = stepDist * Math.cos(rad);
        double dy = -stepDist * Math.sin(rad); // Negative because screen Y goes down

        SwingUtilities.invokeLater(() -> {
            new Timer(20, new java.awt.event.ActionListener() {
                int count = 0;
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    turtle.x += dx;
                    turtle.y += dy;
                    engine.repaint();
                    count++;
                    if (count >= frames) {
                        ((Timer) e.getSource()).stop();
                        latch.countDown();
                    }
                }
            }).start();
        });

        latch.await(); // Pause the code execution thread until the animation finishes
    }

    public static void smoothTurn(GameEnginePanel engine, Turtle turtle, double angle, int frames) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        double stepAngle = angle / frames;

        SwingUtilities.invokeLater(() -> {
            new Timer(20, new java.awt.event.ActionListener() {
                int count = 0;
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    turtle.angle += stepAngle;
                    engine.repaint();
                    count++;
                    if (count >= frames) {
                        ((Timer) e.getSource()).stop();
                        latch.countDown();
                    }
                }
            }).start();
        });

        latch.await();
    }
}
