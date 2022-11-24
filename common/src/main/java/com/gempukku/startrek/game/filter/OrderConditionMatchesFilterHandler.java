package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.OrderAbility;
import com.gempukku.startrek.game.ability.OrderInterruptAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;

public class OrderConditionMatchesFilterHandler extends CardFilterSystem {
    private CardAbilitySystem cardAbilitySystem;
    private ConditionResolverSystem conditionResolverSystem;

    public OrderConditionMatchesFilterHandler() {
        super("orderConditionMatches");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                Array<OrderAbility> orderAbilities = cardAbilitySystem.getCardAbilities(cardEntity, OrderAbility.class);
                Array<OrderInterruptAbility> orderInterruptAbilities = cardAbilitySystem.getCardAbilities(cardEntity, OrderInterruptAbility.class);
                if (orderAbilities.size == 0 && orderInterruptAbilities.size == 0)
                    return false;

                for (OrderAbility orderAbility : orderAbilities) {
                    String orderCondition = orderAbility.getCondition();
                    if (orderCondition == null ||
                            conditionResolverSystem.resolveBoolean(cardEntity, memory, orderCondition)) {
                        return true;
                    }
                }

                for (OrderInterruptAbility orderInterruptAbility : orderInterruptAbilities) {
                    String orderCondition = orderInterruptAbility.getCondition();
                    if (orderCondition == null ||
                            conditionResolverSystem.resolveBoolean(cardEntity, memory, orderCondition)) {
                        return true;
                    }
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
