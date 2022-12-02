package com.gempukku.startrek.server.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.DilemmaType;
import com.gempukku.startrek.card.MissionType;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.mission.AttemptedMissionComponent;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class MissionTypeMatchesAttemptedHandler extends ConditionSystem {
    private CardFilteringSystem cardFilteringSystem;
    private ExecutionStackSystem executionStackSystem;
    private CardLookupSystem cardLookupSystem;
    private IdProviderSystem idProviderSystem;

    public MissionTypeMatchesAttemptedHandler() {
        super("missionTypeMatchesAttempted");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        Entity attemptedMissionEntity = executionStackSystem.getTopMostStackEntityWithComponent(AttemptedMissionComponent.class);
        AttemptedMissionComponent attemptedMission = attemptedMissionEntity.getComponent(AttemptedMissionComponent.class);
        Entity missionEntity = idProviderSystem.getEntityById(attemptedMission.getMissionId());
        final MissionType missionType = cardLookupSystem.getCardDefinition(missionEntity).getMissionType();

        return cardFilteringSystem.hasCard(sourceEntity, memory, "missions", cardFilteringSystem.createAndFilter(parameters),
                new CardFilter() {
                    @Override
                    public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                        DilemmaType dilemmaType = cardLookupSystem.getCardDefinition(cardEntity).getDilemmaType();
                        switch (dilemmaType) {
                            case Dual:
                                return true;
                            case Space:
                                return (missionType == MissionType.Space);
                            case Planet:
                                return (missionType == MissionType.Planet);
                        }
                        return false;
                    }
                });
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        for (String parameter : parameters) {
            cardFilteringSystem.validateFilter(parameter);
        }
    }
}
