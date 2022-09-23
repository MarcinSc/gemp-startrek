package com.gempukku.startrek.server.hall;


import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import com.gempukku.startrek.hall.GameHallPlayerComponent;
import com.gempukku.startrek.server.common.ServerSpawnSystem;

public class HallEntityProviderSystem extends BaseSystem {
    private Entity hallEntity;
    private EntitySubscription playerEntitySubscription;

    private ServerSpawnSystem serverSpawnSystem;

    @Override
    protected void initialize() {
        hallEntity = serverSpawnSystem.spawnEntity("hall/gameHall.template");
        playerEntitySubscription = world.getAspectSubscriptionManager().get(Aspect.all(GameHallPlayerComponent.class));
    }

    public Entity getGameHallEntity() {
        return hallEntity;
    }

    @Override
    protected void processSystem() {

    }

    public Entity getPlayer(String username) {
        IntBag entities = playerEntitySubscription.getEntities();
        for (int i = 0, s = entities.size(); s > i; i++) {
            Entity entity = world.getEntity(entities.get(i));
            GameHallPlayerComponent gameHallPlayer = entity.getComponent(GameHallPlayerComponent.class);
            if (gameHallPlayer.getOwner().equals(username))
                return entity;
        }
        return null;
    }
}
