package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;

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
                FaceUpCardInMissionComponent faceUpCard = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
                if (faceUpCard != null)
                    return missionFilter.accepts(sourceEntity, memory,
                            getMissionCard(faceUpCard.getMissionOwner(), faceUpCard.getMissionIndex()));
                FaceDownCardInMissionComponent faceDownCard = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
                if (faceDownCard != null)
                    return missionFilter.accepts(sourceEntity, memory,
                            getMissionCard(faceDownCard.getMissionOwner(), faceDownCard.getMissionIndex()));
                return false;
            }

            private Entity getMissionCard(String owner, int index) {
                return cardFilteringSystem.findFirstCardInPlay(null, null,
                        "type(Mission),owner(username(" + owner + ")),missionIndex(" + index + ")");
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
