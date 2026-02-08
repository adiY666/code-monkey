package com.monkey.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Turtle extends GameObject {

    private static final Color BODY_COLOR = new Color(34, 139, 34);
    private static final Color HEAD_COLOR = new Color(144, 238, 144); // Light Green
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String FONT_NAME = "Arial";
    private static final int BODY_SIZE = 40;
    private static final int BODY_OFFSET = 20;
    private static final int HEAD_SIZE = 14;
    private static final int HEAD_DIST = 22; // Distance from center

    public int id;
    public double angle;

    public Turtle(double x, double y, int id, double angle) {
        super(x, y);
        this.id = id;
        this.angle = angle;
    }

    public void draw(Graphics2D g2) {
        // Body
        g2.setColor(BODY_COLOR);
        g2.fillOval((int) x - BODY_OFFSET, (int) y - BODY_OFFSET, BODY_SIZE, BODY_SIZE);

        // Head (Direction Indicator)
        double rad = Math.toRadians(angle);
        double headX = x + HEAD_DIST * Math.cos(rad);
        double headY = y - HEAD_DIST * Math.sin(rad); // Y is inverted in graphics

        g2.setColor(HEAD_COLOR);
        g2.fillOval((int) headX - (HEAD_SIZE / 2), (int) headY - (HEAD_SIZE / 2), HEAD_SIZE, HEAD_SIZE);

        // ID Label
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font(FONT_NAME, Font.BOLD, 12));
        g2.drawString("T" + id, (int) x - 6, (int) y + 5);
    }

}
