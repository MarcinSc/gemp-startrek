package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class MatchesConditionHandler extends ConditionSystem {
    private CardFilteringSystem cardFilteringSystem;

    public MatchesConditionHandler() {
        super("matches");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        return cardFilteringSystem.resolveCardFilter(parameters.get(0)).
                accepts(sourceEntity, memory, sourceEntity);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        cardFilteringSystem.validateFilter(parameters.get(0));
    }
}
