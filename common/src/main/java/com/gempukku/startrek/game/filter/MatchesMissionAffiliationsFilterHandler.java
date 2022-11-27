package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class MatchesMissionAffiliationsFilterHandler extends CardFilterSystem {
    private MissionOperations missionOperations;
    private CardLookupSystem cardLookupSystem;
    private CardFilteringSystem cardFilteringSystem;

    public MatchesMissionAffiliationsFilterHandler() {
        super("matchesMissionAffiliations");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardInMissionComponent cardInMission = cardEntity.getComponent(CardInMissionComponent.class);
                Entity mission = missionOperations.findMission(cardInMission.getMissionOwner(), cardInMission.getMissionIndex());
                CardDefinition missionCard = cardLookupSystem.getCardDefinition(mission);
                CardFilter affiliationFilter = cardFilteringSystem.resolveCardFilter(missionCard.getAffiliations());

                return affiliationFilter.accepts(sourceEntity, memory, cardEntity);
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
