package com.gempukku.startrek.server.hall;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.hall.GameHallComponent;
import com.gempukku.startrek.hall.GameHallPlayerComponent;
import com.gempukku.startrek.server.hall.event.PlayerConnected;
import com.gempukku.startrek.server.hall.event.PlayerDisconnected;

public class GameHallSystem extends BaseSystem {
    private SpawnSystem spawnSystem;
    private EventSystem eventSystem;

    private Entity hallEntity;
    private EntitySubscription playerEntitySubscription;

    @Override
    protected void initialize() {
        hallEntity = spawnSystem.spawnEntity("hall/gameHall.template");
        playerEntitySubscription = world.getAspectSubscriptionManager().get(Aspect.all(GameHallPlayerComponent.class));

        spawnSystem.spawnEntities("hall/starterDecks.entities");
    }

    public Entity getGameHallEntity() {
        return hallEntity;
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


    @EventListener
    public void playerConnected(PlayerConnected playerConnected, Entity entity) {
        String username = playerConnected.getUsername();

        GameHallComponent gameHall = hallEntity.getComponent(GameHallComponent.class);
        gameHall.setUserCount(gameHall.getUserCount() + 1);

        eventSystem.fireEvent(EntityUpdated.instance, hallEntity);

        Entity playerEntity = spawnSystem.spawnEntity("hall/gameHallPlayer.template");

        GameHallPlayerComponent player = playerEntity.getComponent(GameHallPlayerComponent.class);
        player.setOwner(username);
        player.setAvatar("red-shirt-male");

        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    @EventListener
    public void playerDisconnected(PlayerDisconnected playerDisconnected, Entity entity) {
        String username = playerDisconnected.getUsername();

        GameHallComponent gameHall = hallEntity.getComponent(GameHallComponent.class);
        gameHall.setUserCount(gameHall.getUserCount() - 1);

        eventSystem.fireEvent(EntityUpdated.instance, hallEntity);

        world.deleteEntity(getPlayer(username));
    }


    @Override
    protected void processSystem() {

    }
}
