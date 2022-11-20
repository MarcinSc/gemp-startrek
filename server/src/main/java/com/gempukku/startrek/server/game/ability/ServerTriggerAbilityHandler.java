package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public class ServerTriggerAbilityHandler extends ServerCardAbilityHandlerSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private GameEffectSystem gameEffectSystem;

    public ServerTriggerAbilityHandler() {
        super("trigger");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String triggerType = cardAbility.getString("triggerType");
        boolean optional = cardAbility.getBoolean("optional", false);
        String condition = cardAbility.getString("condition");
        JsonValue cost = cardAbility.get("cost");
        JsonValue effect = cardAbility.get("effect");
        return new ServerTriggerAbility(triggerType, optional, condition, cost, effect);
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{"triggerType", "condition", "effects"},
                new String[]{"costs", "optional"});

        String condition = cardAbility.getString("condition");
        JsonValue costs = cardAbility.get("costs");
        JsonValue effects = cardAbility.get("effects");

        conditionResolverSystem.validate(condition);
        if (costs != null) {
            validateEffects(costs);
        }
        validateEffects(effects);
    }
}
