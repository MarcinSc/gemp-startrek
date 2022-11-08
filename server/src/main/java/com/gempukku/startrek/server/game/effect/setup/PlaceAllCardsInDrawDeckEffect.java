package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.CardZone;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.deck.PlayerDeckComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class PlaceAllCardsInDrawDeckEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private CardLookupSystem cardLookupSystem;
    private EventSystem eventSystem;

    public PlaceAllCardsInDrawDeckEffect() {
        super("placeAllCardsInDrawDeck");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Entity effectEntity, GameEffectComponent gameEffect, Memory memory) {
        String player = playerResolverSystem.resolvePlayerUsername(effectEntity, memory,
                gameEffect.getDataString("player"));
        Entity playerEntity = playerResolverSystem.findPlayerEntity(player);
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        PlayerPublicStatsComponent playerStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
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
                                playerStats.setDeckCount(playerStats.getDeckCount() + 1);
                            }
                        }
                    }
                });
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }
}
