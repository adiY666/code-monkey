package com.monkey.design;

import com.monkey.core.GameObject;
import com.monkey.core.ITerrain;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.List;

public class TerrainDesign {

    public static void drawTerrain(Graphics2D g2, List<GameObject> objects) {
        if (objects.isEmpty()) return;

        ITerrain terrainType = (ITerrain) objects.get(0);
        int size = terrainType.getSize();
        int r = size / 2;

        g2.setColor(terrainType.getColor());

        for(GameObject o : objects) {
            g2.fillOval((int)o.x - r, (int)o.y - r, size, size);
        }

        // 2. Draw "Connections"
        g2.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for(int i = 0; i < objects.size(); i++) {
            GameObject a = objects.get(i);
            for(int j = i + 1; j < objects.size(); j++) {
                GameObject b = objects.get(j);
                double dist = Math.hypot(a.x - b.x, a.y - b.y);

                if(dist < size + 20) {
                    g2.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
                }
            }
        }
    }
}
