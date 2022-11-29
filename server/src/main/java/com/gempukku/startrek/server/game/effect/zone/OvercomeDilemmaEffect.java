package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.mission.AttemptedMissionComponent;
import com.gempukku.startrek.game.zone.CardZone;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class OvercomeDilemmaEffect extends OneTimeEffectSystem {
    private ExecutionStackSystem executionStackSystem;
    private CardFilteringSystem cardFilteringSystem;
    private EventSystem eventSystem;
    private ZoneOperations zoneOperations;
    private IdProviderSystem idProviderSystem;

    public OvercomeDilemmaEffect() {
        super("overcomeDilemma");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        Entity dilemmaEntity = idProviderSystem.getEntityById(memory.getValue(gameEffect.getDataString("memoryCard")));
        String fromZoneStr = gameEffect.getDataString("fromZone", null);
        CardZone fromZone = (fromZoneStr != null) ? CardZone.valueOf(fromZoneStr) : null;

        AttemptedMissionComponent attemptedMission = executionStackSystem.getTopMostStackEntityWithComponent(AttemptedMissionComponent.class).getComponent(AttemptedMissionComponent.class);
        Entity missionEntity = idProviderSystem.getEntityById(attemptedMission.getMissionId());

        CardComponent card = dilemmaEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        if (fromZone == null || oldZone == fromZone) {
            zoneOperations.attachDilemmaToMission(dilemmaEntity, missionEntity);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"filter"},
                new String[]{"fromZone"});
        cardFilteringSystem.validateFilter(effect.getString("filter"));
        CardZone.valueOf(effect.getString("zone"));
        String fromZone = effect.getString("fromZone");
        if (fromZone != null)
            CardZone.valueOf(fromZone);
    }
}
