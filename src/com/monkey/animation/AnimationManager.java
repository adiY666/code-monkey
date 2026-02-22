package com.monkey.animation;

import com.monkey.gui.GameEnginePanel;
import com.monkey.logic.CollisionChecker;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class AnimationManager {

    private final GameEnginePanel panel;
    private final List<PopEffect> effects = new ArrayList<>();

    public AnimationManager(GameEnginePanel panel) {
        this.panel = panel;
        Timer loop = new Timer(30, e -> update());
        loop.start();
    }

    public void addPop(double x, double y) {
        effects.add(new PopEffect(x, y));
    }

    public void clear() {
        effects.clear();
    }

    private void update() {
        if (effects.isEmpty()) return;
        effects.removeIf(p -> !p.update());
        panel.repaint();
    }

    public void drawEffects(Graphics2D g2) {
        List<PopEffect> currentEffects = new ArrayList<>(effects);
        for (PopEffect p : currentEffects) {
            p.draw(g2);
        }
    }

    public static void smoothStep(GameEnginePanel engine, double totalDist, int frames) throws InterruptedException {
        double stepPerFrame = totalDist / frames;
        double rad = Math.toRadians(engine.monkeyAngle);
        double dx = stepPerFrame * Math.cos(rad);
        double dy = -stepPerFrame * Math.sin(rad);

        for(int i = 0; i < frames; i++) {
            double nextX = engine.monkeyX + dx;
            double nextY = engine.monkeyY + dy;

            if (CollisionChecker.isStoneCollision(engine, nextX, nextY)) {
                break;
            }

            if (CollisionChecker.isWaterCollision(engine, nextX, nextY)) {
                throw new RuntimeException("Monkey fell in the water!");
            }

            engine.monkeyX = nextX;
            engine.monkeyY = nextY;

            engine.checkCollisions();
            engine.repaint();
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

    // --- NEW: Sight Animation! ---
    public static void showSightAnim(GameEnginePanel engine, boolean found) throws InterruptedException {
        if (found) {
            // Found it! Happy little jump
            double origY = engine.monkeyY;
            for(int i = 0; i < 4; i++) { engine.monkeyY -= 4; engine.repaint(); Thread.sleep(15); }
            for(int i = 0; i < 4; i++) { engine.monkeyY += 4; engine.repaint(); Thread.sleep(15); }
            engine.monkeyY = origY;
            engine.repaint();
        } else {
            // Didn't find it! Confused look left and right
            smoothTurn(engine, -30, 6);
            smoothTurn(engine, 60, 12);
            smoothTurn(engine, -30, 6);
        }
        Thread.sleep(100); // Tiny pause before next line of code
    }
}
