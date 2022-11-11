package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class EventAbility implements CardAbility {
    private String condition;
    private Array<JsonValue> costs;
    private Array<JsonValue> effects;

    public EventAbility(String condition, JsonValue costs, JsonValue effects) {
        this.condition = condition;
        this.costs = createArray(costs);
        this.effects = createArray(effects);
    }

    private Array<JsonValue> createArray(JsonValue effects) {
        Array<JsonValue> result = new Array<>();
        if (effects != null) {
            if (effects.type() == JsonValue.ValueType.object) {
                result.add(effects);
            } else {
                for (JsonValue effect : effects) {
                    result.add(effect);
                }
            }
        }
        return result;
    }

    public String getCondition() {
        return condition;
    }

    public Array<JsonValue> getCosts() {
        return costs;
    }

    public Array<JsonValue> getEffects() {
        return effects;
    }
}
