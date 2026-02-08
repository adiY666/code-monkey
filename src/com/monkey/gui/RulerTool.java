package com.monkey.gui;

import com.monkey.core.GameObject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;

public class RulerTool {

    public static final int MODE_NONE = 0;
    public static final int MODE_OBJECT = 1;
    public static final int MODE_FREE = 2;

    // Stylish Colors
    private static final Color LINE_COLOR = new Color(230, 126, 34); // Carrot Orange
    private static final Color DOT_COLOR = new Color(241, 196, 15); // Sunflower Yellow
    private static final Color LABEL_BG = new Color(44, 62, 80, 220); // Dark Blue Transparent
    private static final Color LABEL_TEXT = Color.WHITE;

    private static final int SNAP_DIST = 25;
    private static final Font FONT = new Font("Segoe UI", Font.BOLD, 12);

    // Technical Drawing Style Strokes
    private static final Stroke SOLID = new BasicStroke(2f);
    private static final Stroke DASHED = new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f);

    private final GameEnginePanel engine;
    private int mode = MODE_NONE;
    private Point start = null;
    private Point end = null;

    public RulerTool(GameEnginePanel engine) {
        this.engine = engine;
    }

    public void setMode(int mode) {
        this.mode = mode;
        this.start = null;
        this.end = null;
    }

    public int getMode() { return mode; }

    public void handleClick(int x, int y) {
        if(mode == MODE_NONE) return;
        Point p = new Point(x, y);

        if(mode == MODE_OBJECT) {
            GameObject obj = getObjectAt(x, y);
            if(obj != null) p = new Point((int)obj.x, (int)obj.y);
            else if(Math.hypot(x - engine.monkeyX, y - engine.monkeyY) < SNAP_DIST)
                p = new Point((int)engine.monkeyX, (int)engine.monkeyY);
            else return;
        }

        if(start == null) start = p;
        else if(end == null) end = p;
        else { start = p; end = null; }
    }

    public void draw(Graphics2D g2) {
        if(mode == MODE_NONE || start == null) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Start Point
        drawPoint(g2, start);

        if(end != null) {
            drawPoint(g2, end);

            // Triangle Logic
            int dx = end.x - start.x;
            int dy = end.y - start.y;
            Point corner = new Point(end.x, start.y);

            // Legs (Solid)
            g2.setColor(LINE_COLOR);
            g2.setStroke(SOLID);
            g2.drawLine(start.x, start.y, corner.x, corner.y); // Horizontal
            g2.drawLine(corner.x, corner.y, end.x, end.y);     // Vertical

            // Hypotenuse (Dashed)
            g2.setColor(new Color(230, 126, 34, 150));
            g2.setStroke(DASHED);
            g2.drawLine(start.x, start.y, end.x, end.y);

            // Draw Corner Marker
            int size = 10;
            int cx = (dx > 0) ? corner.x - size : corner.x;
            int cy = (dy > 0) ? corner.y : corner.y - size;
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(cx, cy, size, size);

            drawLabels(g2);
        }
    }

    private void drawPoint(Graphics2D g2, Point p) {
        g2.setColor(DOT_COLOR);
        g2.fillOval(p.x - 5, p.y - 5, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(p.x - 5, p.y - 5, 10, 10);
    }

    private void drawLabels(Graphics2D g2) {
        int dx = end.x - start.x;
        int dy = -(end.y - start.y); // Invert Y for math logic

        // Info Box
        String text1 = String.format("ΔX: %d  ΔY: %d", dx, dy);
        double dist = Math.hypot(dx, dy);
        double ang = Math.toDegrees(Math.atan2(dy, dx));
        if(ang < 0) ang += 360;
        String text2 = String.format("Dist: %.0f  Ang: %.0f°", dist, ang);

        // Position box near the midpoint or corner
        int boxX = end.x + 10;
        int boxY = end.y;

        // Draw Rounded Box
        g2.setFont(FONT);
        int w = 140;
        int h = 40;
        g2.setColor(LABEL_BG);
        g2.fillRoundRect(boxX, boxY, w, h, 10, 10);

        g2.setColor(LABEL_TEXT);
        g2.drawString(text1, boxX + 10, boxY + 16);
        g2.drawString(text2, boxX + 10, boxY + 32);
    }

    private GameObject getObjectAt(int x, int y) {
        for(GameObject b : engine.bananas) if(dist(b, x, y) < SNAP_DIST) return b;
        for(GameObject s : engine.stones) if(dist(s, x, y) < SNAP_DIST) return s;
        for(GameObject t : engine.turtles) if(dist(t, x, y) < SNAP_DIST) return t;
        return null;
    }

    private double dist(GameObject o, int x, int y) {
        return Math.hypot(o.x - x, o.y - y);
    }
}
