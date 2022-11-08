package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class OrderAbility implements CardAbility {
    private JsonValue costs;
    private JsonValue effects;

    public OrderAbility(JsonValue costs, JsonValue effects) {
        this.costs = costs;
        this.effects = effects;
    }

    public JsonValue getCosts() {
        return costs;
    }

    public JsonValue getEffects() {
        return effects;
    }
}
