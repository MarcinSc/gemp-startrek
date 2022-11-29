package com.gempukku.startrek.server.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.condition.ConditionSystem;
import com.gempukku.startrek.game.mission.AttemptedMissionComponent;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class MissionAttemptMatchesHandler extends ConditionSystem {
    private ExecutionStackSystem executionStackSystem;
    private IdProviderSystem idProviderSystem;
    private CardLookupSystem cardLookupSystem;
    private ConditionResolverSystem conditionResolverSystem;

    public MissionAttemptMatchesHandler() {
        super("missionAttemptMatches");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        Entity missionAttemptEntity = executionStackSystem.getTopMostStackEntityWithComponent(AttemptedMissionComponent.class);
        AttemptedMissionComponent mission = missionAttemptEntity.getComponent(AttemptedMissionComponent.class);
        Entity missionEntity = idProviderSystem.getEntityById(mission.getMissionId());
        String requirements = cardLookupSystem.getCardDefinition(missionEntity).getRequirements();
        return conditionResolverSystem.resolveBoolean(sourceEntity, memory, requirements);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
