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
                Array<Entity> missionEntities = cardFilteringSystem.getAllCardsInPlay(sourceEntity, memory, missionFilter);
                CardInMissionComponent cardInMission = cardEntity.getComponent(CardInMissionComponent.class);
                if (cardInMission == null)
                    return false;
                for (Entity missionEntity : missionEntities) {
                    CardInMissionComponent mission = missionEntity.getComponent(CardInMissionComponent.class);
                    if (!mission.getMissionOwner().equals(cardInMission.getMissionOwner()) ||
                            mission.getMissionIndex() != cardInMission.getMissionIndex())
                        return false;
                }

                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        for (String parameter : parameters) {
            cardFilteringSystem.validateFilter(parameter);
        }
    }
}
