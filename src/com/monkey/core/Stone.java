package com.monkey.core;

public class Stone extends GameObject implements ITerrain {

    public Stone(double x, double y) { super(x, y); }

    @Override public String getType() { return "stone"; }

    @Override public boolean isSolid() { return true; }

    @Override public boolean isDeadly() { return false; }
}
