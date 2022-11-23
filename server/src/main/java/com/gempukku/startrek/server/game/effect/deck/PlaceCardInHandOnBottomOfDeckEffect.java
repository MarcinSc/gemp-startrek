package com.gempukku.startrek.server.game.effect.deck;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.function.Consumer;

public class PlaceCardInHandOnBottomOfDeckEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;

    public PlaceCardInHandOnBottomOfDeckEffect() {
        super("placeCardInHandOnBottomOfDeck");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String username = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, gameEffect.getDataString("player"));
        CardFilter filter = cardFilteringSystem.resolveCardFilter(gameEffect.getDataString("filter"));

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

        for (Entity card : cardsToMove) {
            zoneOperations.moveFromCurrentZoneToBottomOfDeck(card);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"player", "filter"},
                new String[]{});
        playerResolverSystem.validatePlayer(effect.getString("player"));
        cardFilteringSystem.validateFilter(effect.getString("filter"));
    }
}
