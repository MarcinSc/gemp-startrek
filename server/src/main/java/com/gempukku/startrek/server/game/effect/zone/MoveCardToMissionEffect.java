package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.zone.CardInHandComponent;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Consumer;

public class MoveCardToMissionEffect extends OneTimeEffectSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private ZoneOperations zoneOperations;

    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<FaceDownCardInMissionComponent> faceDownCardInMissionComponentMapper;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;

    public MoveCardToMissionEffect() {
        super("moveCardToMission");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String filter = gameEffect.getDataString("filter");
        int missionId = Integer.parseInt(memory.getValue(gameEffect.getDataString("missionMemory")));
        boolean faceUp = conditionResolverSystem.resolveBoolean(sourceEntity, memory, gameEffect.getDataString("faceUp"));
        Entity missionEntity = world.getEntity(missionId);

        cardFilteringSystem.forEachCard(sourceEntity, memory, filter,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity cardEntity) {
                        zoneOperations.removeFromCurrentZone(cardEntity);
                        zoneOperations.moveCardToMission(cardEntity, missionEntity, faceUp);
                    }
                });
    }
}
