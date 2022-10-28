package com.gempukku.startrek.game;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
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

    public PlayerPosition getPlayerPosition(String username) {
        return playerPositions.get(username);
    }

    @Override
    protected void processSystem() {

    }
}
