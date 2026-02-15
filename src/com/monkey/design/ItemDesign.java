package com.monkey.design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class ItemDesign {

    public static void drawBanana(Graphics2D g2, double x, double y) {
        AffineTransform old = g2.getTransform();
        g2.translate(x, y);

        g2.setColor(new Color(255, 215, 0)); // Gold Yellow
        g2.setStroke(new BasicStroke(3));

        Path2D banana = new Path2D.Double();
        banana.moveTo(-10, -10);
        banana.quadTo(5, 0, -10, 10);
        banana.quadTo(-5, 0, -10, -10);

        g2.fill(banana);
        g2.setColor(new Color(218, 165, 32)); // Dark Outline
        g2.draw(banana);

        g2.setTransform(old);
    }
}
