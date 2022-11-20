package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;

public class ClientInterruptAbilityHandler extends CardAbilityHandlerSystem {
    private ConditionResolverSystem conditionResolverSystem;

    public ClientInterruptAbilityHandler() {
        super("interrupt");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String condition = cardAbility.getString("condition");
        return new ClientInterruptAbility(condition);
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{"effects"},
                new String[]{"costs", "condition"});

        String condition = cardAbility.getString("condition", null);

        if (condition != null)
            conditionResolverSystem.validateCondition(condition);
    }
}
