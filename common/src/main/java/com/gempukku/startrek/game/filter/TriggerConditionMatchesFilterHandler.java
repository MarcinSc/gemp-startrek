package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.TriggerAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;

public class TriggerConditionMatchesFilterHandler extends CardFilterSystem {
    private CardAbilitySystem cardAbilitySystem;
    private ConditionResolverSystem conditionResolverSystem;

    public TriggerConditionMatchesFilterHandler() {
        super("triggerConditionMatches");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        String triggerType = parameters.get(0);
        String optionalCondition = parameters.get(1);
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                Array<TriggerAbility> triggerAbilities = cardAbilitySystem.getCardAbilities(cardEntity, TriggerAbility.class);
                if (triggerAbilities.size == 0)
                    return false;
                boolean optional = conditionResolverSystem.resolveBoolean(cardEntity, memory, optionalCondition);

                for (TriggerAbility triggerAbility : triggerAbilities) {
                    if (triggerAbility.isOptional() == optional
                            && triggerAbility.getTriggerType().equals(triggerType)) {
                        String triggerCondition = triggerAbility.getCondition();
                        if (triggerCondition == null ||
                                conditionResolverSystem.resolveBoolean(cardEntity, memory, triggerCondition)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 2);
        conditionResolverSystem.validateCondition(parameters.get(1));
    }
}
