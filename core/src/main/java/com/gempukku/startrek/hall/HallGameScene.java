package com.gempukku.startrek.hall;

import com.gempukku.startrek.GameScene;
import com.gempukku.startrek.GameSceneVisitor;

public class HallGameScene implements GameScene {
    @Override
    public <T> T visit(GameSceneVisitor<T> gameSceneVisitor) {
        return gameSceneVisitor.visitHallScene();
    }
}
