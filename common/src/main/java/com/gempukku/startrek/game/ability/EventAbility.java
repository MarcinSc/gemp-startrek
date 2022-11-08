package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class EventAbility implements CardAbility {
    private String condition;
    private JsonValue costs;
    private JsonValue effects;

    public EventAbility(String condition, JsonValue costs, JsonValue effects) {
        this.condition = condition;
        this.costs = costs;
        this.effects = effects;
    }

    public String getCondition() {
        return condition;
    }

    public JsonValue getCosts() {
        return costs;
    }

    public JsonValue getEffects() {
        return effects;
    }
}
