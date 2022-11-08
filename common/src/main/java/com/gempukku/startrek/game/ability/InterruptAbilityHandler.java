package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class InterruptAbilityHandler extends CardAbilityHandlerSystem {
    public InterruptAbilityHandler() {
        super("interrupt");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String condition = cardAbility.getString("condition");
        JsonValue costs = cardAbility.get("costs");
        JsonValue effects = cardAbility.get("effects");
        return new InterruptAbility(condition, costs, effects);
    }
}
