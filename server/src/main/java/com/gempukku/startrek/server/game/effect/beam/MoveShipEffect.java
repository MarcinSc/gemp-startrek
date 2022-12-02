package com.gempukku.startrek.server.game.effect.beam;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.function.Consumer;

public class MoveShipEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private ZoneOperations zoneOperations;

    public MoveShipEffect() {
        super("moveShip");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        Entity missionCardEntity = cardFilteringSystem.findFirstCard(sourceEntity, memory, "inPlay", gameEffect.getDataString("mission"));
        cardFilteringSystem.forEachCard(sourceEntity, memory, "inPlay", new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        zoneOperations.moveShip(entity, missionCardEntity);
                    }
                }, gameEffect.getDataString("ship")
        );
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"ship", "mission"},
                new String[]{});
        cardFilteringSystem.validateFilter(effect.getString("ship"));
        cardFilteringSystem.validateFilter(effect.getString("mission"));
    }
}
