package com.gempukku.startrek.server.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.AndCardFilter;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.mission.AttemptedMissionComponent;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class AttemptingPersonnelFilter extends CardFilterSystem {
    private ExecutionStackSystem executionStackSystem;
    private CardFilteringSystem cardFilteringSystem;
    private IdProviderSystem idProviderSystem;

    public AttemptingPersonnelFilter() {
        super("attemptingPersonnel");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        CardFilter idFilter = new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                Entity attemptedMissionEntity = executionStackSystem.getTopMostStackEntityWithComponent(AttemptedMissionComponent.class);
                if (attemptedMissionEntity == null)
                    return false;
                AttemptedMissionComponent attemptedMission = attemptedMissionEntity.getComponent(AttemptedMissionComponent.class);
                return attemptedMission.getAttemptingPersonnel().contains(idProviderSystem.getEntityId(cardEntity), false);
            }
        };

        return new AndCardFilter(cardFilteringSystem.resolveCardFilter("type(Personnel),unstopped"), idFilter);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
