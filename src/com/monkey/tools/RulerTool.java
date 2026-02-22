package com.monkey.tools;

import com.monkey.core.GameObject;
import com.monkey.gui.GameEnginePanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

public class RulerTool {

    private final GameEnginePanel engine;

    // 0 = Off, 1 = Object-to-Object, 2 = Free Measure
    private int mode = 0;

    // Point 1 (Anchor)
    private boolean hasStart = false;
    private double startX, startY;

    // Point 2 (Locked End)
    private boolean hasEnd = false;
    private double endX, endY;

    public RulerTool(GameEnginePanel engine) {
        this.engine = engine;
    }

    public void setMode(int mode) {
        this.mode = mode;
        this.hasStart = false; // Reset when changing modes
        this.hasEnd = false;
    }

    public int getMode() {
        return mode;
    }

    public void handleClick(int x, int y) {
        if (mode == 0) return;

        if (mode == 1) { // --- OBJECT TO OBJECT MEASURE ---
            Object clicked = engine.getGameObjectAt(x, y);

            if (clicked != null) {
                // Determine the exact X and Y based on what was clicked
                double objX, objY;

                if (clicked instanceof GameObject) {
                    objX = ((GameObject) clicked).x;
                    objY = ((GameObject) clicked).y;
                } else if (clicked.equals("Monkey")) {
                    objX = engine.monkeyX;
                    objY = engine.monkeyY;
                } else {
                    return; // Unknown object
                }

                if (!hasStart || hasEnd) {
                    // Click 1: Start new measurement
                    startX = objX;
                    startY = objY;
                    hasStart = true;
                    hasEnd = false;
                } else {
                    // Click 2: Lock the measurement
                    endX = objX;
                    endY = objY;
                    hasEnd = true;
                }
            }

        } else if (mode == 2) { // --- FREE MEASURE ---
            if (!hasStart || hasEnd) {
                // Click 1: Start new measurement
                startX = x;
                startY = y;
                hasStart = true;
                hasEnd = false;
            } else {
                // Click 2: Lock the measurement
                endX = x;
                endY = y;
                hasEnd = true;
            }
        }
    }

    public void draw(Graphics2D g2, Point currentMouse) {
        if (mode == 0 || !hasStart) return;
        if (!hasEnd && currentMouse == null) return;

        // Use the locked end coordinates if we clicked twice, otherwise track the mouse
        double targetX = hasEnd ? endX : currentMouse.x;
        double targetY = hasEnd ? endY : currentMouse.y;

        // Draw Line
        g2.setColor(new Color(255, 165, 0, 200)); // Orange semi-transparent
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0));
        g2.drawLine((int) startX, (int) startY, (int) targetX, (int) targetY);

        // Draw Start Point Dot
        g2.fillOval((int) startX - 5, (int) startY - 5, 10, 10);

        // Draw End Point Dot (if locked)
        if (hasEnd) {
            g2.fillOval((int) endX - 5, (int) endY - 5, 10, 10);
        }

        // Calculate Distance
        String text = getString(targetX, targetY);

        g2.setFont(new Font("Consolas", Font.BOLD, 12));
        int textWidth = g2.getFontMetrics().stringWidth(text) + 10;

        // Background box for readability
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect((int) startX + 10, (int) startY - 25, textWidth, 20);

        g2.setColor(Color.WHITE);
        g2.drawString(text, (int) startX + 15, (int) startY - 11);
    }

    private String getString(double targetX, double targetY) {
        double dx = targetX - startX;
        double dy = targetY - startY;
        int distance = (int) Math.hypot(dx, dy);

        // Calculate Angle (0 is Right, 90 is Up)
        double angle = Math.toDegrees(Math.atan2(-dy, dx));
        if (angle < 0) angle += 360;

        // --- FIX IS HERE ---
        // distance, (int)dx, and (int)-dy are passed to %d
        // angle is passed to %.0f
        return String.format("Dist: %d | X:%d Y:%d | ∠ %.0f°", distance, (int) dx, (int) -dy, angle);
    }
}
