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
        CardFilter cardFilter = cardFilteringSystem.createAndFilter(parameters, 1);
        return cardFilteringSystem.hasCard(sourceEntity, memory, parameters.get(0), cardFilter);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        cardFilteringSystem.validateSource(parameters.get(0));
        cardFilteringSystem.validateFilter(parameters, 1);
    }
}
