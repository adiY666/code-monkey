package com.monkey.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Turtle extends GameObject {

    // --- CONSTANTS ---
    private static final Color BODY_COLOR = new Color(34, 139, 34); // Dark Green
    private static final Color HEAD_COLOR = new Color(144, 238, 144); // Light Green
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String FONT_NAME = "Arial";
    private static final int BODY_SIZE = 30; // Slightly smaller to fit grid
    private static final int BODY_OFFSET = 15;
    private static final int HEAD_SIZE = 12;
    private static final int HEAD_DIST = 18; // Distance from center

    // --- FIELDS ---
    public int id;
    public int type; // 0=Normal, 1=Fast (Kept for save system compatibility)
    public double angle;

    public Turtle(double x, double y, int id, double angle) {
        super(x, y);
        this.id = id;
        this.type = 0; // Default type
        this.angle = angle;
    }

    // Constructor including type (for loading from JSON)
    public Turtle(double x, double y, int id, int type, double angle) {
        super(x, y);
        this.id = id;
        this.type = type;
        this.angle = angle;
    }

    public void draw(Graphics2D g2) {
        // 1. Draw Body
        g2.setColor(BODY_COLOR);
        g2.fillOval((int) x - BODY_OFFSET, (int) y - BODY_OFFSET, BODY_SIZE, BODY_SIZE);

        // 2. Draw Head (Direction Indicator)
        // Note: We subtract Y because in Swing, Y goes "Down", so "Up" is negative Y.
        double rad = Math.toRadians(angle);
        double headX = x + HEAD_DIST * Math.cos(rad);
        double headY = y - HEAD_DIST * Math.sin(rad);

        g2.setColor(HEAD_COLOR);
        g2.fillOval((int) headX - (HEAD_SIZE / 2), (int) headY - (HEAD_SIZE / 2), HEAD_SIZE, HEAD_SIZE);

        // 3. Draw ID Label
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font(FONT_NAME, Font.BOLD, 12));
        // Center the text roughly
        g2.drawString("T" + id, (int) x - 6, (int) y + 5);
    }

    // Logic for moving the turtle
    public void step(double dist) {
        double rad = Math.toRadians(angle);
        this.x += dist * Math.cos(rad);
        this.y -= dist * Math.sin(rad);
    }

    public void turn(double deg) {
        this.angle += deg;
    }
}
