package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class HasInSameMissionFilterHandler extends CardFilterSystem {
    private CardFilteringSystem cardFilteringSystem;

    public HasInSameMissionFilterHandler() {
        super("hasInSameMission");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        CardFilter filter = cardFilteringSystem.createAndFilter(parameters);
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardInMissionComponent cardInMission = cardEntity.getComponent(CardInMissionComponent.class);
                String missionOwner = cardInMission.getMissionOwner();
                int missionIndex = cardInMission.getMissionIndex();

                CardFilter sameMissionFilter = cardFilteringSystem.resolveCardFilter("inMission(owner(username(" + missionOwner + "))," + missionIndex + ")");

                AndCardFilter resultFilter = new AndCardFilter(sameMissionFilter, filter);

                return cardFilteringSystem.findFirstCard(sourceEntity, memory, resultFilter) != null;
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
