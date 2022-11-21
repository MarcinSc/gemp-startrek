package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class MissionMatchesFilterHandler extends CardFilterSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;

    public MissionMatchesFilterHandler() {
        super("missionMatches");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        CardFilter missionFilter = cardFilterResolverSystem.createAndFilter(parameters);
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardInMissionComponent cardInMission = cardEntity.getComponent(CardInMissionComponent.class);
                if (cardInMission != null)
                    return missionFilter.accepts(sourceEntity, memory,
                            getMissionCard(cardInMission.getMissionOwner(), cardInMission.getMissionIndex()));
                return false;
            }

            private Entity getMissionCard(String owner, int index) {
                return cardFilteringSystem.findFirstCardInPlay(null, null,
                        "type(Mission),inMission(username(" + owner + ")," + index + ")");
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        for (String parameter : parameters) {
            cardFilterResolverSystem.validateFilter(parameter);
        }
    }
}
