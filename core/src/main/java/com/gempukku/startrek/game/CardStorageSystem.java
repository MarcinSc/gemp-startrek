package com.gempukku.startrek.game;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public class CardStorageSystem extends BaseSystem {
    private PlayerPositionSystem playerPositionSystem;

    private ObjectMap<PlayerPosition, PlayerZones> playerCardsMap = new ObjectMap<>();

    public PlayerZones getPlayerCards(String username) {
        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        return getPlayerCards(playerPosition);
    }

    public PlayerZones getPlayerCards(PlayerPosition playerPosition) {
        PlayerZones playerZones = playerCardsMap.get(playerPosition);
        if (playerZones == null) {
            playerZones = new PlayerZones();
            playerCardsMap.put(playerPosition, playerZones);
        }
        return playerZones;
    }

    public Entity findRenderedCard(Entity cardEntity) {
        for (PlayerZones playerZones : playerCardsMap.values()) {
            Entity renderedCard = playerZones.findRenderedCard(cardEntity);
            if (renderedCard != null)
                return renderedCard;
        }
        return null;
    }

    @Override
    protected void processSystem() {

    }
}
