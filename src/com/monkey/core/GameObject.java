package com.monkey.core;

import java.awt.*;

public abstract class GameObject implements IGameObject {

    public double x;
    public double y;
    public Rectangle hitbox;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle((int) x - 20, (int) y - 20, 40, 40);
    }

    @Override
    public double getX() { return x; }

    @Override
    public double getY() { return y; }

    public void updateHitbox() {
        this.hitbox.setLocation((int) x - 20, (int) y - 20);
    }

    // Every specific object that extends this MUST define its type!
    @Override
    public abstract String getType();
}
