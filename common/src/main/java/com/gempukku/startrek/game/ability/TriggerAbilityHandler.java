package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class TriggerAbilityHandler extends CardAbilityHandlerSystem {
    public TriggerAbilityHandler() {
        super("trigger");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String triggerType = cardAbility.getString("triggerType");
        boolean optional = cardAbility.getBoolean("optional", false);
        String condition = cardAbility.getString("condition");
        JsonValue cost = cardAbility.get("cost");
        JsonValue effect = cardAbility.get("effect");
        return new TriggerAbility(triggerType, optional, condition, cost, effect);
    }
}
