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

public class MoveShipEffect extends OneTimeEffectSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;

    public MoveShipEffect() {
        super("moveShip");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        Entity missionCardEntity = cardFilteringSystem.findFirstCardInPlay(sourceEntity, memory, gameEffect.getDataString("mission"));
        cardFilteringSystem.forEachCardInPlay(sourceEntity, memory, gameEffect.getDataString("ship"),
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        zoneOperations.moveShip(entity, missionCardEntity);
                    }
                });
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"ship", "mission"},
                new String[]{});
        cardFilterResolverSystem.validateFilter(effect.getString("ship"));
        cardFilterResolverSystem.validateFilter(effect.getString("mission"));
    }
}
