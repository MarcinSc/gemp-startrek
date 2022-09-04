package com.gempukku.startrek.login;

import com.gempukku.startrek.GameScene;
import com.gempukku.startrek.GameSceneVisitor;

public class LoginGameScene implements GameScene {
    @Override
    public <T> T visit(GameSceneVisitor<T> gameSceneVisitor) {
        return gameSceneVisitor.visitLoginScene();
    }
}
