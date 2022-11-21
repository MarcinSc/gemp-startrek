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

public class BeamToMissionEffect extends OneTimeEffectSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;

    public BeamToMissionEffect() {
        super("beamToMission");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String shipFilter = gameEffect.getDataString("ship");
        String cardFilter = gameEffect.getDataString("filter");
        Entity shipEntity = cardFilteringSystem.findFirstCardInPlay(sourceEntity, memory, shipFilter);
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, cardFilter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        zoneOperations.unattachFromShip(shipEntity, entity);
                    }
                });
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"ship", "filter"},
                new String[]{});
        cardFilterResolverSystem.validateFilter(effect.getString("ship"));
        cardFilterResolverSystem.validateFilter(effect.getString("filter"));
    }
}
