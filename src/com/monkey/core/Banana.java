package com.monkey.core;

public class Banana extends GameObject implements IEntity {

    public Banana(double x, double y) { super(x, y); }

    @Override public String getType() { return "banana"; }

    @Override public boolean isCollectible() { return true; }
}
