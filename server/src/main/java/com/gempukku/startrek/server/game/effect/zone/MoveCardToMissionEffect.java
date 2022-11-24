package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MoveCardToMissionEffect extends OneTimeEffectSystem {
    private CardFilteringSystem cardFilteringSystem;
    private ConditionResolverSystem conditionResolverSystem;
    private EventSystem eventSystem;
    private ZoneOperations zoneOperations;
    private ServerEntityIdSystem serverEntityIdSystem;

    public MoveCardToMissionEffect() {
        super("moveCardToMission");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String filter = gameEffect.getDataString("filter");
        String missionId = memory.getValue(gameEffect.getDataString("missionMemory"));
        boolean faceUp = conditionResolverSystem.resolveBoolean(sourceEntity, memory, gameEffect.getDataString("faceUp"));
        Entity missionEntity = serverEntityIdSystem.findfromId(missionId);

        cardFilteringSystem.forEachCard(sourceEntity, memory, new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        zoneOperations.moveFromCurrentZoneToMission(cardEntity, missionEntity, faceUp);
                    }
                }, filter
        );
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"filter", "missionMemory", "faceUp"},
                new String[]{});
        cardFilteringSystem.validateFilter(effect.getString("filter"));
        conditionResolverSystem.validateCondition(effect.getString("faceUp"));
    }
}
