package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;

public class ConditionFilterHandler extends CardFilterSystem {
    private ConditionResolverSystem conditionResolverSystem;

    public ConditionFilterHandler() {
        super("condition");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
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
}
