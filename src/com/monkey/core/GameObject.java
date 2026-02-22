package com.monkey.core;

public abstract class GameObject implements IGameObject {

    public double x;
    public double y;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() { return x; }

    @Override
    public double getY() { return y; }

    // Every specific object that extends this MUST define its type!
    @Override
    public abstract String getType();
}
