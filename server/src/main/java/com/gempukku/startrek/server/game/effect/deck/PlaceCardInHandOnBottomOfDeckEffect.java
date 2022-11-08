package com.gempukku.startrek.server.game.effect.deck;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.MoveCardToZoneEffect;

import java.util.function.Consumer;

public class PlaceCardInHandOnBottomOfDeckEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private DeckOperationsSystem deckOperationsSystem;
    private MoveCardToZoneEffect moveCardToZoneEffect;

    public PlaceCardInHandOnBottomOfDeckEffect() {
        super("placeCardInHandOnBottomOfDeck");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String username = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, gameEffect.getDataString("player"));
        CardFilter filter = cardFilterResolverSystem.resolveCardFilter(gameEffect.getDataString("filter"));

        Array<Entity> cardsToMove = new Array<>();
        cardFilteringSystem.forEachCardInHand(username,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        if (filter.accepts(sourceEntity, memory, cardEntity)) {
                            cardsToMove.add(cardEntity);
                        }
                    }
                });
        cardsToMove.shuffle();

        deckOperationsSystem.putCardsOnBottomOfDeck(cardsToMove);
    }
}
