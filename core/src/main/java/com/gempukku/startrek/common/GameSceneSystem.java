package com.gempukku.startrek.common;

import com.artemis.BaseSystem;
import com.gempukku.startrek.GameScene;

public class GameSceneSystem extends BaseSystem {
    private GameScene nextGameScene;

    public void setNextGameScene(GameScene nextGameScene) {
        this.nextGameScene = nextGameScene;
    }

    public GameScene consumeNextGameScene() {
        if (nextGameScene != null) {
            GameScene result = nextGameScene;
            nextGameScene = null;
            return result;
        }
        return null;
    }

    @Override
    protected void processSystem() {

    }
}
