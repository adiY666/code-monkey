package com.monkey.core;

import com.monkey.design.TurtleDesign; // --- ADD THIS IMPORT ---
import java.awt.Graphics2D;

public class Turtle extends GameObject {

    public int id, type;
    public double angle;

    public Turtle(double x, double y, int id, int type) {
        this(x, y, id, type, 0.0);
    }

    public Turtle(double x, double y, int id, int type, double angle) {
        super(x, y);
        this.id = id;
        this.type = type;
        this.angle = angle;
    }

    @Override public String getType() { return "turtle"; }

    public void draw(Graphics2D g2) {
        TurtleDesign.draw(g2, this.x, this.y, this.angle, this.id);
    }
}
