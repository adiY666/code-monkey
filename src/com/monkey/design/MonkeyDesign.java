package com.monkey.design;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class MonkeyDesign {

    // --- COLORS ---
    // Defined here for easy changing later
    private static final Color HEAD_COLOR = new Color(160, 82, 45); // Sienna Brown
    // Using the face color for ears makes them look like inner ears from top-down
    private static final Color FACE_AND_EAR_COLOR = new Color(255, 228, 196); // Skin Tone
    private static final Color EYE_COLOR = Color.BLACK;

    public static void draw(Graphics2D g2, double x, double y, double angle) {
        AffineTransform old = g2.getTransform();
        g2.translate(x, y);
        // Rotate (negative angle because Swing Y axis is inverted)
        g2.rotate(Math.toRadians(-angle));

        // 1. Ears (Now using the lighter face color)
        g2.setColor(FACE_AND_EAR_COLOR);
        g2.fillOval(-8, -22, 10, 10); // Right Ear (Top in standard view)
        g2.fillOval(-8, 12, 10, 10);  // Left Ear (Bottom in standard view)

        // 2. Head (Main Circle - drawn over the back part of the ears)
        g2.setColor(HEAD_COLOR);
        g2.fillOval(-15, -15, 30, 30);

        // 3. Face/Muzzle (Lighter oval on top of the head)
        g2.setColor(FACE_AND_EAR_COLOR);
        g2.fillOval(-5, -12, 18, 24);

        // 4. Eyes (Looking "Forward" relative to rotation)
        g2.setColor(EYE_COLOR);
        g2.fillOval(5, -6, 5, 5);
        g2.fillOval(5, 1, 5, 5);

        // (Hands and Tail removed as requested)

        g2.setTransform(old);
    }
}
