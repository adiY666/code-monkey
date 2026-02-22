package com.monkey.design;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class TurtleDesign {

    // --- DESIGN CONSTANTS ---
    private static final Color BODY_COLOR = new Color(34, 139, 34); // Forest Green
    private static final Color HEAD_COLOR = new Color(107, 142, 35); // Olive Drab
    private static final Color TEXT_COLOR = Color.WHITE;

    private static final int BODY_SIZE = 36;
    private static final int BODY_OFFSET = BODY_SIZE / 2;

    private static final int HEAD_SIZE = 16;
    private static final int HEAD_DIST = 20; // Distance from center of body to center of head

    private static final String FONT_NAME = "Consolas";

    public static void draw(Graphics2D g2, double x, double y, double angle, int id) {

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
        g2.drawString("T" + id, (int) x - 8, (int) y + 4);
    }
}
