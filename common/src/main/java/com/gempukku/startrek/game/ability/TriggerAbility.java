package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class TriggerAbility implements CardAbility {
    private String triggerType;
    private boolean optional;
    private String condition;
    private Array<JsonValue> costs;
    private Array<JsonValue> effects;

    public TriggerAbility(String triggerType, boolean optional, String condition,
                          JsonValue costs, JsonValue effects) {
        this.triggerType = triggerType;
        this.optional = optional;
        this.condition = condition;
        this.costs = createArray(costs, true);
        this.effects = createArray(effects, false);
    }

    private Array<JsonValue> createArray(JsonValue effects, boolean cost) {
        Array<JsonValue> result = new Array<>();
        if (effects != null) {
            if (effects.type() == JsonValue.ValueType.object) {
                effects.addChild("cost", new JsonValue(cost));
                result.add(effects);
            } else {
                for (JsonValue effect : effects) {
                    effect.addChild("cost", new JsonValue(cost));
                }
                result.add(effects);
            }
        }
        return result;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public boolean isOptional() {
        return optional;
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
