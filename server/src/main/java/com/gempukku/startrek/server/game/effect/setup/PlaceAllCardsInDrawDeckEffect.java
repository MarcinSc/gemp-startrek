package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.Entity;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.server.game.card.CardComponent;
import com.gempukku.startrek.server.game.card.CardLookupSystem;
import com.gempukku.startrek.server.game.card.CardZone;
import com.gempukku.startrek.server.game.deck.PlayerDeckComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;

import java.util.function.Consumer;

public class PlaceAllCardsInDrawDeckEffect extends EffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private CardLookupSystem cardLookupSystem;

    public PlaceAllCardsInDrawDeckEffect() {
        super("placeAllCardsInDrawDeck");
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String player = playerResolverSystem.resolvePlayerUsername(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("player"));
        Entity playerEntity = playerResolverSystem.findPlayerEntity(player);
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        LazyEntityUtil.forEachEntityWithComponent(world, CardComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        CardComponent cardComponent = entity.getComponent(CardComponent.class);
                        if (cardComponent.getOwner().equals(player)) {
                            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardComponent.getCardId());
                            if (cardDefinition.getType() != CardType.Dilemma && cardDefinition.getType() != CardType.Mission) {
                                cardComponent.setCardZone(CardZone.DECK);
                                deck.getCards().add(entity.getId());
                            }
                        }
                    }
                });
        removeEffectFromStack(gameEffectEntity);
    }
}
