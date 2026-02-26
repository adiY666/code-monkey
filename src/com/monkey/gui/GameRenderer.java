package com.monkey.gui;

import com.monkey.core.GameObject;
import com.monkey.core.Turtle;
import com.monkey.design.EditorDesign;
import com.monkey.design.ItemDesign;
import com.monkey.design.MonkeyDesign;
import com.monkey.design.TerrainDesign;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Timer;

public class GameRenderer {

    private final GameEnginePanel engine;
    private final List<PopEffect> effects = new ArrayList<>();

    public GameRenderer(GameEnginePanel engine) {
        this.engine = engine;
        // Run animation effect loop at ~30 FPS
        new Timer(30, e -> updateEffects()).start();
    }

    public void addPop(double x, double y) {
        effects.add(new PopEffect(x, y));
    }

    public void clearEffects() {
        effects.clear();
    }

    private void updateEffects() {
        if (effects.isEmpty()) return;
        Iterator<PopEffect> it = effects.iterator();
        while (it.hasNext()) {
            PopEffect p = it.next();
            p.life--;
            p.radius += 2;
            if (p.life <= 0) it.remove();
        }
        engine.repaint();
    }

    // This is called by the GameEnginePanel's paintComponent method!
    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        TerrainDesign.drawRivers(g2, engine.rivers);
        TerrainDesign.drawStones(g2, engine.stones);

        for (Turtle t : engine.turtles) t.draw(g2);
        for (GameObject b : engine.bananas) ItemDesign.drawBanana(g2, b.x, b.y);

        engine.sightTool.draw(g2);
        MonkeyDesign.draw(g2, engine.monkeyX, engine.monkeyY, engine.monkeyAngle);

        for (PopEffect p : effects) {
            g2.setColor(new Color(255, 255, 0, Math.max(0, p.life * 10)));
            g2.setStroke(new BasicStroke(3));
            g2.drawOval((int) p.x - p.radius, (int) p.y - p.radius, p.radius * 2, p.radius * 2);
        }

        if (!engine.getGhostTool().equals("none")) {
            EditorDesign.drawGhost(g2, engine.getGhostTool(), engine.getGhostX(), engine.getGhostY());
        }

        engine.rulerTool.draw(g2, engine.getCurrentMouse());
    }

    // Moved the effect class here since only the renderer cares about it!
    private static class PopEffect {
        double x, y;
        int radius = 5;
        int life = 20;

        PopEffect(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
