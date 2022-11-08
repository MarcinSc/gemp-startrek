package com.gempukku.startrek.server.game.effect.discard;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.CardZone;
import com.gempukku.startrek.game.PlayerDiscardPileComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class DiscardOperationsSystem extends BaseSystem {
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;

    public void putCardsOnBottomOfDeck(Array<Entity> cards) {
        for (Entity cardEntity : cards) {
            CardComponent card = cardEntity.getComponent(CardComponent.class);
            Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
            PlayerDiscardPileComponent deck = playerEntity.getComponent(PlayerDiscardPileComponent.class);
            deck.getCards().add(cardEntity.getId());
            card.setCardZone(CardZone.DISCARD_PILE);

            eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        }
    }

    @Override
    protected void processSystem() {

    }
}
