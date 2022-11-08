package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class EventAbilityHandler extends CardAbilityHandlerSystem {
    public EventAbilityHandler() {
        super("event");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String condition = cardAbility.getString("condition", null);
        JsonValue costs = cardAbility.get("costs");
        JsonValue effects = cardAbility.get("effects");
        return new EventAbility(condition, costs, effects);
    }
}
