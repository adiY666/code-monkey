package com.monkey.core;

import java.awt.Color;

public class River extends GameObject implements ITerrain {

    public River(double x, double y) { super(x, y); }

    @Override public String getType() { return "river"; }

    @Override public boolean isSolid() { return false; }

    @Override public boolean isDeadly() { return true; }

    @Override public Color getColor() { return new Color(52, 152, 219); }

    @Override public int getSize() { return 50; }
}
