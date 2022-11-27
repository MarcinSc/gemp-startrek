package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.mission.MissionComponent;

public class CompletedMissionFilterHandler extends CardFilterSystem {
    public CompletedMissionFilterHandler() {
        super("completedMission");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                MissionComponent mission = cardEntity.getComponent(MissionComponent.class);
                return mission != null && mission.isCompleted();
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
