package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
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
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                Array<EventAbility> eventAbilities = cardAbilitySystem.getCardAbilities(cardEntity, EventAbility.class);
                if (eventAbilities.size == 0)
                    return true;
                for (EventAbility cardAbility : eventAbilities) {
                    String condition = cardAbility.getCondition();
                    if (condition == null ||
                            conditionResolverSystem.resolveBoolean(sourceEntity, memory, condition))
                        return true;
                }
                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
