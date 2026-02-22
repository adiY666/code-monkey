package com.monkey.core;

public interface ITerrain extends IGameObject {

    boolean isSolid();  // True for Stones (monkey can't walk through)
    boolean isDeadly(); // True for Rivers (monkey drowns)
}
