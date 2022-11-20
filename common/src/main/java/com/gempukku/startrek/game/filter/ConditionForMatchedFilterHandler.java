package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;

public class ConditionForMatchedFilterHandler extends CardFilterSystem {
    private ConditionResolverSystem conditionResolverSystem;

    public ConditionForMatchedFilterHandler() {
        super("conditionForMatched");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                for (String parameter : parameters) {
                    if (!conditionResolverSystem.resolveBoolean(cardEntity, memory, parameter))
                        return false;
                }

                return true;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        for (String parameter : parameters) {
            conditionResolverSystem.validateCondition(parameter);
        }
    }
}
