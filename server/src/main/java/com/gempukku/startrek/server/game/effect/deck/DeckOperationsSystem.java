package com.gempukku.startrek.server.game.effect.deck;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.CardZone;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.deck.PlayerDeckComponent;

public class DeckOperationsSystem extends BaseSystem {
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;

    public void putCardsOnBottomOfDeck(Array<Entity> cards) {
        for (Entity cardEntity : cards) {
            CardComponent card = cardEntity.getComponent(CardComponent.class);
            Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
            PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
            PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            deck.getCards().insert(0, cardEntity.getId());
            card.setCardZone(CardZone.DECK);

            stats.setDeckCount(stats.getDeckCount() + 1);

            eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        }
    }

    @Override
    protected void processSystem() {

    }
}
