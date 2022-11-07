package com.gempukku.startrek.server.game.deck;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.CardZone;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.hand.CardInHandComponent;
import com.gempukku.startrek.server.game.amount.AmountResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

public class DrawCardEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private EventSystem eventSystem;

    public DrawCardEffect() {
        super("drawCard");
    }

    @Override
    protected void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        String player = gameEffect.getDataString("player");
        Entity playerEntity = playerResolverSystem.resolvePlayer(gameEffectEntity, memory, player);
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);

        int cardsDrawn = 0;
        Array<Integer> cards = deck.getCards();
        if (cards.size > 0) {
            Entity cardEntity = world.getEntity(cards.removeIndex(cards.size - 1));

            CardComponent card = cardEntity.getComponent(CardComponent.class);
            card.setCardZone(CardZone.HAND);
            CardInHandComponent cardInHand = cardInHandComponentMapper.create(cardEntity);
            cardInHand.setOwner(card.getOwner());
            eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
            cardsDrawn++;
        }

        PlayerPublicStatsComponent playerPublicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        playerPublicStats.setHandCount(playerPublicStats.getHandCount() + cardsDrawn);
        playerPublicStats.setDeckCount(playerPublicStats.getDeckCount() - cardsDrawn);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }
}
