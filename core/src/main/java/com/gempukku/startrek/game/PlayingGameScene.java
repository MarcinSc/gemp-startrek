package com.gempukku.startrek.game;

import com.gempukku.startrek.GameScene;
import com.gempukku.startrek.GameSceneVisitor;

public class PlayingGameScene implements GameScene {
    private String gameId;

    public PlayingGameScene(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public <T> T visit(GameSceneVisitor<T> gameSceneVisitor) {
        return gameSceneVisitor.visitPlayingGameScene(gameId);
    }
}
