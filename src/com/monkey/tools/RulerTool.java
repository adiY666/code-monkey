package com.monkey.tools;

import com.monkey.core.GameObject;
import com.monkey.design.EditorDesign;
import com.monkey.gui.GameEnginePanel;
import java.awt.Graphics2D;
import java.awt.Point;

public class RulerTool {

    private final GameEnginePanel engine;

    // Coordinates
    private int startX, startY;
    private int endX, endY;

    // State: 0 = Idle, 1 = Measuring (Follows Mouse), 2 = Finished (Static)
    private int state = 0;
    private int mode = 0; // 0=None, 1=Object, 2=Free

    public RulerTool(GameEnginePanel engine) {
        this.engine = engine;
    }

    public void setMode(int mode) {
        this.mode = mode;
        reset();
    }

    public int getMode() {
        return mode;
    }

    public void reset() {
        this.state = 0;
        this.startX = 0;
        this.startY = 0;
        engine.repaint();
    }

    public void handleClick(int x, int y) {
        if (mode == 0) return;

        // STATE 2 (Finished) -> Click 3 starts a NEW measurement
        if (state == 2) {
            reset();
            // Fall through to start new measurement immediately...
        }

        // STATE 0 (Idle) -> Click 1 sets START
        if (state == 0) {
            if (mode == 1) { // Object Mode
                GameObject o = engine.getGameObjectAt(x, y);
                if (o != null) {
                    this.startX = (int) o.x;
                    this.startY = (int) o.y;
                    this.state = 1; // Start measuring
                }
            } else { // Free Mode
                this.startX = x;
                this.startY = y;
                this.state = 1; // Start measuring
            }
        }
        // STATE 1 (Measuring) -> Click 2 sets END and FREEZES
        else if (state == 1) {
            if (mode == 1) { // Object Mode
                GameObject o = engine.getGameObjectAt(x, y);
                if (o != null) {
                    this.endX = (int) o.x;
                    this.endY = (int) o.y;
                    this.state = 2; // Freeze
                }
            } else { // Free Mode
                this.endX = x;
                this.endY = y;
                this.state = 2; // Freeze
            }
        }

        engine.repaint();
    }

    public void draw(Graphics2D g2, Point mousePos) {
        if (mode == 0 || state == 0) return;

        Point target = mousePos;

        // If finished, draw to the stored End Point, not the mouse
        if (state == 2) {
            target = new Point(endX, endY);
        }

        // Draw the ruler using the design class
        EditorDesign.drawRuler(g2, startX, startY, target);
    }
}
