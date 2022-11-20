package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.function.Consumer;

public class PlaceAllCardsInDrawDeckEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private CardLookupSystem cardLookupSystem;
    private EventSystem eventSystem;
    private ZoneOperations zoneOperations;

    public PlaceAllCardsInDrawDeckEffect() {
        super("placeAllCardsInDrawDeck");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String player = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory,
                gameEffect.getDataString("player"));
        LazyEntityUtil.forEachEntityWithComponent(world, CardComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        CardComponent cardComponent = cardEntity.getComponent(CardComponent.class);
                        if (cardComponent.getOwner().equals(player)) {
                            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardComponent.getCardId());
                            if (cardDefinition.getType() != CardType.Dilemma && cardDefinition.getType() != CardType.Mission) {
                                zoneOperations.moveCardToTopOfDeck(cardEntity);
                            }
                        }
                    }
                });
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"player"},
                new String[]{});
        playerResolverSystem.validate(effect.getString("player"));
    }
}
