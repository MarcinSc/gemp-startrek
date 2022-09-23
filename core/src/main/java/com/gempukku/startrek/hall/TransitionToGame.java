package com.gempukku.startrek.hall;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.common.GameSceneSystem;
import com.gempukku.startrek.common.ServerStateChanged;

public class TransitionToGame extends EntitySystem {
    private GameSceneSystem gameSceneSystem;

    private Entity playedGame;

    public TransitionToGame() {
        super(Aspect.all(PlayedGameComponent.class));
    }

    @Override
    public void inserted(Entity e) {
        playedGame = e;
    }

    @EventListener
    public void update(ServerStateChanged serverStateChanged, Entity entity) {
        if (playedGame != null) {
            PlayedGameComponent playedGameComponent = playedGame.getComponent(PlayedGameComponent.class);
            String gameId = playedGameComponent.getGameId();
            //gameSceneSystem.setNextGameScene(new PlayingGameScene(gameId));
        }
    }

    @Override
    protected void processSystem() {

    }
}
