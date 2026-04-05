package com.monkey.tools;

import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import com.monkey.gui.game.GameEnginePanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.HashSet;
import java.util.Set;

public class SightTool {

    private final GameEnginePanel engine;
    private boolean active = false;

    public SightTool(GameEnginePanel engine) {
        this.engine = engine;
    }

    public void toggle() {
        this.active = !this.active;
    }

    public boolean isActive() {
        return active;
    }

    public void draw(Graphics2D g2) {
        if (!active) return;

        int maxRadius = 400;
        double fov = 60.0;
        int numRays = 150;

        double startAngle = engine.monkeyAngle - (fov / 2);
        double angleStep = fov / (numRays - 1);

        Polygon visionCone = new Polygon();
        visionCone.addPoint((int) engine.monkeyX, (int) engine.monkeyY);

        Set<GameObject> visibleObjects = new HashSet<>();

        for (int r = 0; r < numRays; r++) {
            double currentAngle = startAngle + (r * angleStep);
            double rad = Math.toRadians(currentAngle);
            double dx = Math.cos(rad);
            double dy = -Math.sin(rad);

            double checkX = engine.monkeyX;
            double checkY = engine.monkeyY;
            boolean hitObstacle = false;

            for (int d = 0; d <= maxRadius; d += 2) {
                checkX = engine.monkeyX + (dx * d);
                checkY = engine.monkeyY + (dy * d);

                for (Turtle t : engine.turtles) {
                    if (Math.hypot(t.x - checkX, t.y - checkY) < 20) {
                        visibleObjects.add(t);
                    }
                }

                for (GameObject b : engine.bananas) {
                    if (Math.hypot(b.x - checkX, b.y - checkY) < 20) {
                        visibleObjects.add(b);
                    }
                }

                for (GameObject stone : engine.stones) {
                    if (stone.hitbox != null && stone.hitbox.contains(checkX, checkY)) {
                        hitObstacle = true;
                        break;
                    }
                }

                if (hitObstacle) break;
            }

            visionCone.addPoint((int) checkX, (int) checkY);
        }

        g2.setColor(new Color(255, 255, 0, 80));
        g2.fill(visionCone);

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2));
        for (GameObject obj : visibleObjects) {
            g2.drawOval((int) obj.x - 22, (int) obj.y - 22, 44, 44);
        }
    }
}
