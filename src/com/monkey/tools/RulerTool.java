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

    public RulerTool(GameEnginePanel engine) {
        this.engine = engine;
    }

    public void setMode(int mode) {
        this.mode = mode;
        this.hasStart = false; // Reset when changing modes
    }

    public int getMode() {
        return mode;
    }

    public void handleClick(int x, int y) {
        if (mode == 0) return;

        if (mode == 1) { // --- OBJECT TO OBJECT MEASURE ---

            // FIX: Use Object instead of GameObject
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

                if (!hasStart) {
                    startX = objX;
                    startY = objY;
                    hasStart = true;
                } else {
                    hasStart = false; // Clicked second object, reset for next measure
                }
            }

        } else if (mode == 2) { // --- FREE MEASURE ---
            if (!hasStart) {
                startX = x;
                startY = y;
                hasStart = true;
            } else {
                hasStart = false;
            }
        }
    }

    public void draw(Graphics2D g2, Point currentMouse) {
        if (mode == 0 || !hasStart || currentMouse == null) return;

        // Draw Line
        g2.setColor(new Color(255, 165, 0, 200)); // Orange semi-transparent
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0));
        g2.drawLine((int) startX, (int) startY, currentMouse.x, currentMouse.y);

        // Draw Start Point Dot
        g2.fillOval((int) startX - 5, (int) startY - 5, 10, 10);

        // Calculate Distance
        double dx = currentMouse.x - startX;
        double dy = currentMouse.y - startY;
        int distance = (int) Math.hypot(dx, dy);

        // Calculate Angle (0 is Right, 90 is Up)
        double angle = Math.toDegrees(Math.atan2(-dy, dx));
        if (angle < 0) angle += 360;

        // Draw Measurement Text box
        String text = String.format("Dist: %d | Ang: %d°", distance, (int) angle);
        int midX = (int) (startX + currentMouse.x) / 2;
        int midY = (int) (startY + currentMouse.y) / 2;

        g2.setFont(new Font("Arial", Font.BOLD, 14));
        int textWidth = g2.getFontMetrics().stringWidth(text);

        // Background for text
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(midX - textWidth / 2 - 5, midY - 20, textWidth + 10, 24, 8, 8);

        // Text
        g2.setColor(Color.WHITE);
        g2.drawString(text, midX - textWidth / 2, midY - 3);
    }
}
