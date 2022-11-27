package com.gempukku.startrek.server.game.effect.stack;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.mission.AttemptedMissionComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class PlaceAttemptedMissionOnStackEffect extends OneTimeEffectSystem {
    private ZoneOperations zoneOperations;
    private ExecutionStackSystem executionStackSystem;

    public PlaceAttemptedMissionOnStackEffect() {
        super("placeAttemptedMissionOnStack");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        Entity attemptedMissionEntity = executionStackSystem.getTopMostStackEntityWithComponent(AttemptedMissionComponent.class);

        zoneOperations.moveEffectToStack(attemptedMissionEntity, 0);
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{},
                new String[]{});
    }
}
