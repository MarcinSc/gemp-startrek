package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class OrderAbilityHandler extends CardAbilityHandlerSystem {
    public OrderAbilityHandler() {
        super("order");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        JsonValue costs = cardAbility.get("costs");
        JsonValue effects = cardAbility.get("effects");
        return new OrderAbility(costs, effects);
    }
}
