package com.gempukku.startrek.server.game.deck;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.CardInHandComponent;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.server.game.amount.AmountResolverSystem;
import com.gempukku.startrek.server.game.card.CardComponent;
import com.gempukku.startrek.server.game.card.CardZone;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

public class DrawCardsEffect extends EffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private AmountResolverSystem amountResolverSystem;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private EventSystem eventSystem;

    public DrawCardsEffect() {
        super("drawCards");
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        Entity playerEntity = playerResolverSystem.resolvePlayer(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("player"));
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        int amount = amountResolverSystem.resolveAmount(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("amount"));

        int cardsDrawn = 0;
        Array<Integer> cards = deck.getCards();
        while (cards.size > 0 && amount > 0) {
            Entity cardEntity = world.getEntity(cards.removeIndex(cards.size - 1));

            CardComponent card = cardEntity.getComponent(CardComponent.class);
            card.setCardZone(CardZone.HAND);
            CardInHandComponent cardInHand = cardInHandComponentMapper.create(cardEntity);
            cardInHand.setOwner(card.getOwner());
            cardInHand.setCardId(card.getCardId());
            eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
            cardsDrawn++;

            amount--;
        }

        PlayerPublicStatsComponent playerPublicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        playerPublicStats.setHandCount(playerPublicStats.getHandCount() + cardsDrawn);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        removeEffectFromStack(gameEffectEntity);
    }
}
