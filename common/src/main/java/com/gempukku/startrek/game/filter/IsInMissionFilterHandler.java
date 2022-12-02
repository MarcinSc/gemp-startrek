package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class IsInMissionFilterHandler extends CardFilterSystem {
    private CardFilteringSystem cardFilteringSystem;

    public IsInMissionFilterHandler() {
        super("isInMission");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        CardFilter missionFilter = cardFilteringSystem.createAndFilter(parameters);
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardInMissionComponent cardInMission = cardEntity.getComponent(CardInMissionComponent.class);
                if (cardInMission == null)
                    return false;

                Entity missionEntity = cardFilteringSystem.findFirstCard(sourceEntity, memory, parameters.get(0), "any");
                CardInMissionComponent mission = missionEntity.getComponent(CardInMissionComponent.class);
                if (!mission.getMissionOwner().equals(cardInMission.getMissionOwner()) ||
                        mission.getMissionIndex() != cardInMission.getMissionIndex())
                    return false;

                return true;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        cardFilteringSystem.validateSource(parameters.get(0));
    }
}
