package com.monkey.animation;

import com.monkey.gui.game.GameEnginePanel;
import com.monkey.engine.CollisionChecker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;

public class AnimationManager {

    private final GameEnginePanel panel;
    private final List<PopEffect> effects = new ArrayList<>();

    // Tracks active background animations (like turtles)
    private final List<Object> transitions = Collections.synchronizedList(new ArrayList<>());

    public AnimationManager(GameEnginePanel panel) {
        this.panel = panel;
        Timer loop = new Timer(30, e -> update());
        loop.start();
    }

    // --- NEW: Sync Logic for the Interpreter ---
    public boolean isAnimating() {
        return !transitions.isEmpty();
    }

    public void update() {
        if (!effects.isEmpty()) {
            effects.removeIf(p -> !p.update());
            panel.repaint();
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

    public static void showSightAnim(GameEnginePanel engine, boolean found) throws InterruptedException {
        if (found) {
            // Found it! Happy little jump
            double origY = engine.monkeyY;
            for(int i = 0; i < 4; i++) { engine.monkeyY -= 4; engine.repaint(); Thread.sleep(15); }
            for(int i = 0; i < 4; i++) { engine.monkeyY += 4; engine.repaint(); Thread.sleep(15); }
            engine.monkeyY = origY;
            engine.repaint();
        } else {
            smoothTurn(engine, -30, 6);
            smoothTurn(engine, 60, 12);
            smoothTurn(engine, -30, 6);
        }
        Thread.sleep(100);
    }
}
