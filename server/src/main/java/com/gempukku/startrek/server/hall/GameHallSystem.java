package com.gempukku.startrek.server.hall;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.hall.GameHallComponent;
import com.gempukku.startrek.hall.GameHallPlayerComponent;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import com.gempukku.startrek.server.hall.event.PlayerConnected;
import com.gempukku.startrek.server.hall.event.PlayerDisconnected;

public class GameHallSystem extends BaseSystem {
    private HallEntityProviderSystem hallEntityProviderSystem;
    private ServerSpawnSystem spawnSystem;
    private EventSystem eventSystem;

    @EventListener
    public void playerConnected(PlayerConnected playerConnected, Entity entity) {
        String username = playerConnected.getUsername();

        Entity gameHallEntity = hallEntityProviderSystem.getGameHallEntity();
        GameHallComponent gameHall = gameHallEntity.getComponent(GameHallComponent.class);
        gameHall.setUserCount(gameHall.getUserCount() + 1);

        eventSystem.fireEvent(EntityUpdated.instance, gameHallEntity);

        Entity playerEntity = spawnSystem.spawnEntity("hall/gameHallPlayer.template");

        GameHallPlayerComponent player = playerEntity.getComponent(GameHallPlayerComponent.class);
        player.setOwner(username);
        player.setPortrait("professor-x");

        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    @EventListener
    public void playerDisconnected(PlayerDisconnected playerDisconnected, Entity entity) {
        String username = playerDisconnected.getUsername();

        Entity gameHallEntity = hallEntityProviderSystem.getGameHallEntity();
        GameHallComponent gameHall = gameHallEntity.getComponent(GameHallComponent.class);
        gameHall.setUserCount(gameHall.getUserCount() - 1);

        eventSystem.fireEvent(EntityUpdated.instance, gameHallEntity);

        world.deleteEntity(hallEntityProviderSystem.getPlayer(username));
    }


    @Override
    protected void processSystem() {

    }
}
