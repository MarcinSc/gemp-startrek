package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public class ServerOrderAbilityHandler extends ServerCardAbilityHandlerSystem {
    private GameEffectSystem gameEffectSystem;
    private ConditionResolverSystem conditionResolverSystem;

    public ServerOrderAbilityHandler() {
        super("order");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String condition = cardAbility.getString("condition", null);
        JsonValue costs = cardAbility.get("costs");
        JsonValue effects = cardAbility.get("effects");
        return new ServerOrderAbility(condition, costs, effects);
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
            conditionResolverSystem.validate(condition);
        if (costs != null) {
            validateEffects(costs);
        }
        validateEffects(effects);
    }
}
