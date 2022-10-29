package com.gempukku.startrek.game;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.common.AuthenticationHolderSystem;

public class PlayerPositionSystem extends BaseEntitySystem {
    private ObjectMap<String, PlayerPosition> playerPositions = new ObjectMap<>();

    private AuthenticationHolderSystem authenticationHolderSystem;

    public PlayerPositionSystem() {
        super(Aspect.all(GamePlayerComponent.class));
    }

    @Override
    protected void inserted(int entityId) {
        GamePlayerComponent player = world.getEntity(entityId).getComponent(GamePlayerComponent.class);
        String playerName = player.getName();
        if (authenticationHolderSystem.getUsername().equals(playerName)) {
            playerPositions.put(playerName, PlayerPosition.Lower);
        } else {
            if (playerPositions.size == 1) {
                playerPositions.put(playerName, PlayerPosition.Upper);
            } else {
                playerPositions.put(playerName, PlayerPosition.Lower);
            }
        }
    }

    public Entity getPlayerEntity(String username) {
        IntBag playerIds = getSubscription().getEntities();
        for (int i = 0; i < playerIds.size(); i++) {
            Entity playerEntity = world.getEntity(playerIds.get(i));
            GamePlayerComponent player = playerEntity.getComponent(GamePlayerComponent.class);
            if (player.getName().equals(username))
                return playerEntity;
        }
        return null;
    }

    public PlayerPosition getPlayerPosition(String username) {
        return playerPositions.get(username);
    }

    public ObjectMap<String, PlayerPosition> getPlayerPositions() {
        return playerPositions;
    }

    @Override
    protected void processSystem() {

    }
}
