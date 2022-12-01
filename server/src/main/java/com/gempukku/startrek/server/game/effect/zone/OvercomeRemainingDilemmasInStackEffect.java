package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.GameEntityProvider;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.mission.AttemptedMissionComponent;
import com.gempukku.startrek.server.game.deck.HiddenDilemmaStackComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class OvercomeRemainingDilemmasInStackEffect extends OneTimeEffectSystem {
    private GameEntityProvider gameEntityProvider;
    private ZoneOperations zoneOperations;
    private ExecutionStackSystem executionStackSystem;
    private IdProviderSystem idProviderSystem;

    public OvercomeRemainingDilemmasInStackEffect() {
        super("overcomeRemainingDilemmasInStack");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        Entity gameEntity = gameEntityProvider.getGameEntity();
        HiddenDilemmaStackComponent dilemmaStack = gameEntity.getComponent(HiddenDilemmaStackComponent.class);

        AttemptedMissionComponent attemptedMission = executionStackSystem.getTopMostStackEntityWithComponent(AttemptedMissionComponent.class).getComponent(AttemptedMissionComponent.class);
        Entity missionEntity = idProviderSystem.getEntityById(attemptedMission.getMissionId());

        while (dilemmaStack.getCards().size > 0) {
            Entity dilemmaEntity = zoneOperations.removeTopCardFromDilemmaStack();
            zoneOperations.attachDilemmaToMission(dilemmaEntity, missionEntity);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{},
                new String[]{});
    }
}
