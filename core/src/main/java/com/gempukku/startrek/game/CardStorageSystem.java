package com.gempukku.startrek.game;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public class CardStorageSystem extends BaseSystem {
    private PlayerPositionSystem playerPositionSystem;

    private ObjectMap<PlayerPosition, PlayerCards> playerCardsMap = new ObjectMap<>();

    public PlayerCards getPlayerCards(String username) {
        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        return getPlayerCards(playerPosition);
    }

    public PlayerCards getPlayerCards(PlayerPosition playerPosition) {
        PlayerCards playerCards = playerCardsMap.get(playerPosition);
        if (playerCards == null) {
            playerCards = new PlayerCards();
            playerCardsMap.put(playerPosition, playerCards);
        }
        return playerCards;
    }

    public Entity findRenderedCard(Entity cardEntity) {
        for (PlayerCards playerCards : playerCardsMap.values()) {
            Entity renderedCard = playerCards.findRenderedCard(cardEntity);
            if (renderedCard != null)
                return renderedCard;
        }
        return null;
    }

    @Override
    protected void processSystem() {

    }
}
