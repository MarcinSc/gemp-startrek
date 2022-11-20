package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.zone.CardInHandComponent;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MoveCardToMissionEffect extends OneTimeEffectSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private ConditionResolverSystem conditionResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private ZoneOperations zoneOperations;
    private ServerEntityIdSystem serverEntityIdSystem;

    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<FaceDownCardInMissionComponent> faceDownCardInMissionComponentMapper;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;

    public MoveCardToMissionEffect() {
        super("moveCardToMission");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String filter = gameEffect.getDataString("filter");
        String missionId = memory.getValue(gameEffect.getDataString("missionMemory"));
        boolean faceUp = conditionResolverSystem.resolveBoolean(sourceEntity, memory, gameEffect.getDataString("faceUp"));
        Entity missionEntity = serverEntityIdSystem.findfromId(missionId);

        cardFilteringSystem.forEachCard(sourceEntity, memory, filter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        zoneOperations.removeFromCurrentZone(cardEntity);
                        zoneOperations.moveCardToMission(cardEntity, missionEntity, faceUp);
                    }
                });
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"filter", "missionMemory", "faceUp"},
                new String[]{});
        cardFilterResolverSystem.validateFilter(effect.getString("filter"));
        conditionResolverSystem.validateCondition(effect.getString("faceUp"));
    }
}
