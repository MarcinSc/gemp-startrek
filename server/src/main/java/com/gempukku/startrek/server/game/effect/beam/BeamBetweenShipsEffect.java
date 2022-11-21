package com.gempukku.startrek.server.game.effect.beam;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.function.Consumer;

public class BeamBetweenShipsEffect extends OneTimeEffectSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;

    public BeamBetweenShipsEffect() {
        super("beamBetweenShips");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String fromShipFilter = gameEffect.getDataString("fromShip");
        String toShipFilter = gameEffect.getDataString("toShip");
        String cardFilter = gameEffect.getDataString("filter");
        Entity fromShipEntity = cardFilteringSystem.findFirstCardInPlay(sourceEntity, memory, fromShipFilter);
        Entity toShipEntity = cardFilteringSystem.findFirstCardInPlay(sourceEntity, memory, toShipFilter);
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, cardFilter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        zoneOperations.attachFromShipToShip(fromShipEntity, toShipEntity, entity);
                    }
                });
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"fromShip", "toShip", "filter"},
                new String[]{});
        cardFilterResolverSystem.validateFilter(effect.getString("fromShip"));
        cardFilterResolverSystem.validateFilter(effect.getString("toShip"));
        cardFilterResolverSystem.validateFilter(effect.getString("filter"));
    }
}
