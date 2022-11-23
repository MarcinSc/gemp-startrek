package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class CanBeginEngagementInvolvingPersonnelConditionHandler extends ConditionSystem {
    private CardFilteringSystem cardFilteringSystem;

    public CanBeginEngagementInvolvingPersonnelConditionHandler() {
        super("canBeginEngagementInvolvingPersonnel");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        CardFilter yourShip = cardFilteringSystem.resolveCardFilter(
                "type(Ship)," +
                        "missionMatches(not(missionType(Headquarters)))," +
                        "staffed," +
                        "hasOnBoard(" + StringUtils.merge(parameters, ",") + ")," +
                        "missionHas(type(Ship),not(owner(self)))");
        return cardFilteringSystem.findFirstCardInPlay(sourceEntity, memory, yourShip) != null;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        for (String parameter : parameters) {
            cardFilteringSystem.validateFilter(parameter);
        }
    }
}
