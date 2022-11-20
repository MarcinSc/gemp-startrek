package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;

public class MatchesConditionHandler extends ConditionSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;

    public MatchesConditionHandler() {
        super("matches");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        return cardFilterResolverSystem.resolveCardFilter(parameters.get(0)).
                accepts(sourceEntity, memory, sourceEntity);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        cardFilterResolverSystem.validate(parameters.get(0));
    }
}
