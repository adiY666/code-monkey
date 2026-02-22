package com.monkey.core;

public class River extends GameObject implements ITerrain {

    public River(double x, double y) { super(x, y); }

    @Override public String getType() { return "river"; }

    @Override public boolean isSolid() { return false; }

    @Override public boolean isDeadly() { return true; }
}
