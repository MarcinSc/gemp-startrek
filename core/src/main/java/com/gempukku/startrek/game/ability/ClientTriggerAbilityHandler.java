package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;

public class ClientTriggerAbilityHandler extends CardAbilityHandlerSystem {
    private ConditionResolverSystem conditionResolverSystem;

    public ClientTriggerAbilityHandler() {
        super("trigger");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String triggerType = cardAbility.getString("triggerType");
        boolean optional = cardAbility.getBoolean("optional", false);
        String condition = cardAbility.getString("condition");
        return new TriggerAbility(triggerType, optional, condition);
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{"triggerType", "condition", "effects"},
                new String[]{"costs", "optional"});

        String condition = cardAbility.getString("condition");

        conditionResolverSystem.validateCondition(condition);
    }
}
