package com.monkey.core;

import com.monkey.design.TurtleDesign; // --- ADD THIS IMPORT ---
import java.awt.Graphics2D;

public class Turtle implements IGameObject {

    public double x, y;
    public int id, type;
    public double angle;

    public Turtle(double x, double y, int id, int type) {
        this(x, y, id, type, 0.0);
    }

    public Turtle(double x, double y, int id, int type, double angle) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.type = type;
        this.angle = angle;
    }

    @Override public String getType() { return "turtle"; }

    @Override public double getX() { return x; }

    @Override public double getY() { return y; }

    public void step(double distance) {
        double rad = Math.toRadians(angle);
        x += distance * Math.cos(rad);
        y -= distance * Math.sin(rad);
    }

    public void turn(double turnAngle) {
        this.angle += turnAngle;
    }

    // --- CHANGED: Now simply passes data to the Design class! ---
    public void draw(Graphics2D g2) {
        TurtleDesign.draw(g2, this.x, this.y, this.angle, this.id);
    }
}
