package com.monkey.core;

import java.awt.Color;
import java.awt.Rectangle;

public class Stone extends GameObject implements ITerrain {

    public Stone(double x, double y) {
        super(x, y);
        this.hitbox = new Rectangle((int) x - 20, (int) y - 20, 50, 50);
    }

    @Override public String getType() { return "stone"; }

    @Override public boolean isSolid() { return true; }

    @Override public boolean isDeadly() { return false; }

    @Override public Color getColor() { return new Color(100, 100, 100); }

    @Override public int getSize() { return 45; }
}
