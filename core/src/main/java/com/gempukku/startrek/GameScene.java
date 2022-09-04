package com.gempukku.startrek;

public interface GameScene {
    <T> T visit(GameSceneVisitor<T> gameSceneVisitor);
}
