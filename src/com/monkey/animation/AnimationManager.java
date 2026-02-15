package com.monkey.animation;

import com.monkey.gui.GameEnginePanel;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Timer;

public class AnimationManager {

    private final GameEnginePanel panel;
    private final List<PopEffect> effects = new ArrayList<>();
    private final Timer loop;

    public AnimationManager(GameEnginePanel panel) {
        this.panel = panel;

        // Run animation loop at ~30 FPS
        this.loop = new Timer(30, e -> update());
        this.loop.start();
    }

    public void addPop(double x, double y) {
        effects.add(new PopEffect(x, y));
    }

    public void clear() {
        effects.clear();
    }

    private void update() {
        if (effects.isEmpty()) return;

        Iterator<PopEffect> it = effects.iterator();
        while (it.hasNext()) {
            PopEffect p = it.next();
            if (!p.update()) {
                it.remove();
            }
        }
        panel.repaint();
    }

    public void drawEffects(Graphics2D g2) {
        // Clone list to avoid ConcurrentModificationException during drawing
        List<PopEffect> currentEffects = new ArrayList<>(effects);
        for (PopEffect p : currentEffects) {
            p.draw(g2);
        }
    }

    // Static helper for smooth movement (Logic from CodeExecutor)
    public static void smoothStep(GameEnginePanel engine, double totalDist, int frames) throws InterruptedException {
        double stepPerFrame = totalDist / frames;
        for(int i = 0; i < frames; i++) {
            engine.step(stepPerFrame);
            engine.checkCollisions();
            Thread.sleep(20);
        }
    }

    public static void smoothTurn(GameEnginePanel engine, double totalAngle, int frames) throws InterruptedException {
        double anglePerFrame = totalAngle / frames;
        for(int i = 0; i < frames; i++) {
            engine.monkeyAngle += anglePerFrame;
            engine.repaint();
            Thread.sleep(20);
        }
    }
}
