package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class HasCardConditionHandler extends ConditionSystem {
    private CardFilteringSystem cardFilteringSystem;

    public HasCardConditionHandler() {
        super("hasCard");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        CardFilter cardFilter = cardFilteringSystem.createAndFilter(parameters);
        Entity card = cardFilteringSystem.findFirstCardInPlay(sourceEntity, memory, cardFilter);
        return card != null;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        for (String parameter : parameters) {
            cardFilteringSystem.validateFilter(parameter);
        }
    }
}
