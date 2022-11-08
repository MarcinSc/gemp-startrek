package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.EventAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;

public class PlayableFilterHandler extends CardFilterSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private CardAbilitySystem cardAbilitySystem;

    public PlayableFilterHandler() {
        super("playable");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, ObjectMap<String, String> memory, Entity cardEntity) {
                EventAbility cardAbility = cardAbilitySystem.getCardAbility(cardEntity, EventAbility.class);
                if (cardAbility != null) {
                    String condition = cardAbility.getCondition();
                    if (condition != null && !conditionResolverSystem.resolveBoolean(sourceEntity, memory, condition)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
