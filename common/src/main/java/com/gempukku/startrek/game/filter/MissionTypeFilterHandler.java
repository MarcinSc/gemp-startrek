package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.MissionType;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;

public class MissionTypeFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;

    public MissionTypeFilterHandler() {
        super("missionType");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        MissionType missionType = MissionType.valueOf(parameters.get(0));
        return new MissionTypeCardFilter(missionType);
    }

    private class MissionTypeCardFilter implements CardFilter {
        private MissionType missionType;

        public MissionTypeCardFilter(MissionType missionType) {
            this.missionType = missionType;
        }

        @Override
        public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity.getComponent(CardComponent.class).getCardId());
            return cardDefinition.getMissionType() == missionType;
        }
    }
}
