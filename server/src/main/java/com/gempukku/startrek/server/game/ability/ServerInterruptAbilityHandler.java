package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public class ServerInterruptAbilityHandler extends ServerCardAbilityHandlerSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private GameEffectSystem gameEffectSystem;

    public ServerInterruptAbilityHandler() {
        super("interrupt");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String condition = cardAbility.getString("condition");
        JsonValue costs = cardAbility.get("costs");
        JsonValue effects = cardAbility.get("effects");
        return new ServerInterruptAbility(condition, costs, effects);
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{"effects"},
                new String[]{"costs", "condition"});

        String condition = cardAbility.getString("condition", null);
        JsonValue costs = cardAbility.get("costs");
        JsonValue effects = cardAbility.get("effects");

        if (condition != null)
            conditionResolverSystem.validateCondition(condition);
        if (costs != null) {
            validateEffects(costs);
        }
        validateEffects(effects);
    }
}
