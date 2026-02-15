package com.monkey.animation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class PopEffect {
    public double x, y;
    public int radius = 5;
    public int life = 20; // Frames to live

    public PopEffect(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean update() {
        life--;
        radius += 2; // Expand
        return life > 0; // Return false if dead
    }

    public void draw(Graphics2D g2) {
        // Fade out based on life
        int alpha = Math.max(0, Math.min(255, life * 12));
        g2.setColor(new Color(255, 215, 0, alpha)); // Gold color
        g2.setStroke(new BasicStroke(3));
        g2.drawOval((int)x - radius, (int)y - radius, radius * 2, radius * 2);
    }
}
