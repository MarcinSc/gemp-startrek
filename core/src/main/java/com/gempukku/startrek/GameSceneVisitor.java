package com.gempukku.startrek;

public interface GameSceneVisitor<T> {
    T visitLoginScene();

    T visitHallScene();

    T visitPlayingGameScene(String gameId);
}
