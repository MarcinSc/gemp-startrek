package com.gempukku.startrek.hall;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;

public class GameHallPlayerProviderSystem extends EntitySystem {
    private Entity playerEntity;

    public GameHallPlayerProviderSystem() {
        super(Aspect.all(GameHallPlayerComponent.class));
    }

    public Entity getPlayerEntity() {
        return playerEntity;
    }

    @Override
    public void inserted(Entity e) {
        playerEntity = e;
    }

    @Override
    public void removed(Entity e) {
        playerEntity = null;
    }

    @Override
    protected void processSystem() {

    }
}
