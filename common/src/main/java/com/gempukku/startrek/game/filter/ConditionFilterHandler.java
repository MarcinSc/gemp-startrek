package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
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
            public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
                for (String parameter : parameters) {
                    if (!conditionResolverSystem.resolveBoolean(cardEntity, memory, parameter))
                        return false;
                }

                return true;
            }
        };
    }
}
