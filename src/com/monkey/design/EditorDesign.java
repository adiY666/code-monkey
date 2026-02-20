package com.monkey.design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

public class EditorDesign {

    public static void drawGhost(Graphics2D g2, String tool, int x, int y) {
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.5f));

        switch (tool) {
            case "Banana": ItemDesign.drawBanana(g2, x, y); break;
            case "Stone": g2.setColor(Color.GRAY); g2.fillOval(x - 20, y - 20, 40, 40); break;
            case "River": g2.setColor(new Color(52, 152, 219)); g2.fillOval(x - 25, y - 25, 50, 50); break;
            case "Turtle": g2.setColor(new Color(34, 139, 34)); g2.fillOval(x - 15, y - 15, 30, 30); break;
            case "Spawn":
                // --- FIX: Draw the actual Monkey as the ghost instead of a purple circle! ---
                MonkeyDesign.draw(g2, x, y, 0);
                break;
        }
        g2.setComposite(java.awt.AlphaComposite.SrcOver);
    }

    public static void drawRuler(Graphics2D g2, int startX, int startY, Point mousePos) {
        if (startX == 0 || mousePos == null) return;

        int endX = mousePos.x;
        int endY = mousePos.y;

        // 1. Draw Line
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{10}, 0));
        g2.drawLine(startX, startY, endX, endY);

        // 2. Draw Start/End dots
        g2.fillOval(startX - 3, startY - 3, 6, 6);
        g2.fillOval(endX - 3, endY - 3, 6, 6);

        // 3. Calculate Stats
        double dist = Math.hypot(endX - startX, endY - startY);
        int dx = endX - startX;
        int dy = endY - startY; // Screen Y goes down

        // Calculate Angle (Standard Math: Right=0, Up=90)
        double angle = Math.toDegrees(Math.atan2(-dy, dx));
        if (angle < 0) angle += 360; // Keep it 0-360

        // 4. Draw Info Box
        String text = String.format("Dist: %.0f | X:%d Y:%d | ∠ %.0f°", dist, dx, -dy, angle);

        g2.setFont(new Font("Consolas", Font.BOLD, 12));
        int textWidth = g2.getFontMetrics().stringWidth(text) + 10;

        // Background box for readability
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(startX + 10, startY - 25, textWidth, 20);

        g2.setColor(Color.WHITE);
        g2.drawString(text, startX + 15, startY - 11);
    }
}
