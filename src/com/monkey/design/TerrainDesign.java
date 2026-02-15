package com.monkey.design;

import com.monkey.core.GameObject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class TerrainDesign {

    public static void drawRivers(Graphics2D g2, List<GameObject> rivers) {
        drawConnected(g2, rivers, new Color(52, 152, 219), 50);
    }

    public static void drawStones(Graphics2D g2, List<GameObject> stones) {
        drawConnected(g2, stones, new Color(100, 100, 100), 45);
    }

    // Generic logic to connect adjacent objects
    private static void drawConnected(Graphics2D g2, List<GameObject> objs, Color c, int size) {
        g2.setColor(c);
        int r = size / 2;

        // 1. Draw the "Body" of each object
        for(GameObject o : objs) {
            g2.fillOval((int)o.x - r, (int)o.y - r, size, size);
        }

        // 2. Draw "Connections" between close neighbors
        g2.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for(int i = 0; i < objs.size(); i++) {
            GameObject a = objs.get(i);
            for(int j = i + 1; j < objs.size(); j++) {
                GameObject b = objs.get(j);
                double dist = Math.hypot(a.x - b.x, a.y - b.y);

                // If they are close (adjacent grid cells + wiggle room)
                if(dist < size + 20) {
                    g2.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
                }
            }
        }
    }
}
