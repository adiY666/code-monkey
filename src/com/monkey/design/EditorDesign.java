package com.monkey.design;

import java.awt.Color;
import java.awt.Graphics2D;

public class EditorDesign {

    public static void drawGhost(Graphics2D g2, String tool, int x, int y) {
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.5f));

        switch (tool) {
            case "Banana": ItemDesign.drawBanana(g2, x, y); break;
            case "Stone": g2.setColor(Color.GRAY); g2.fillOval(x - 20, y - 20, 40, 40); break;
            case "River": g2.setColor(new Color(52, 152, 219)); g2.fillOval(x - 25, y - 25, 50, 50); break;
            case "Turtle": g2.setColor(new Color(34, 139, 34)); g2.fillOval(x - 15, y - 15, 30, 30); break;
            case "Spawn": MonkeyDesign.draw(g2, x, y, 0); break;
        }
        g2.setComposite(java.awt.AlphaComposite.SrcOver);
    }
}
