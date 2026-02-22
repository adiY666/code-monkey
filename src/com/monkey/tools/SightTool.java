package com.monkey.tools;

import com.monkey.gui.GameEnginePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

public class SightTool {

    private final GameEnginePanel engine;
    private boolean active = false;

    public SightTool(GameEnginePanel engine) {
        this.engine = engine;
    }

    public void toggle() {
        this.active = !this.active;
    }

    public void draw(Graphics2D g2) {
        if (!active) return;

        // Draw a 60-degree yellow vision cone extending from the monkey
        g2.setColor(new Color(255, 255, 0, 80)); // Semi-transparent yellow
        int radius = 400; // How far the monkey can see

        // Arc2D draws the pie slice. We center it on the monkey and point it at the monkey's angle.
        double startAngle = engine.monkeyAngle - 30;
        g2.fill(new Arc2D.Double(engine.monkeyX - radius, engine.monkeyY - radius, radius * 2, radius * 2, startAngle, 60, Arc2D.PIE));
    }
}
