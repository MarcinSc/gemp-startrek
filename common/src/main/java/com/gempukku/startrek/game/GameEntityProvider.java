package com.gempukku.startrek.game;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;

public class GameEntityProvider extends EntitySystem {
    private Entity gameEntity;

    public GameEntityProvider() {
        super(Aspect.all(GameComponent.class));
    }

    @Override
    public void inserted(Entity e) {
        gameEntity = e;
    }

    public Entity getGameEntity() {
        return gameEntity;
    }

    @Override
    protected void processSystem() {

    }
}
