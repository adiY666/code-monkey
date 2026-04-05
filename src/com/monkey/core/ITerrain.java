package com.monkey.core;

import java.awt.Color;

public interface ITerrain extends IGameObject {

    boolean isSolid();  // True for Stones (monkey can't walk through)

    boolean isDeadly(); // True for Rivers (monkey drowns)

    Color getColor();   // The color used to draw the terrain

    int getSize();      // The radius/thickness of the terrain
}