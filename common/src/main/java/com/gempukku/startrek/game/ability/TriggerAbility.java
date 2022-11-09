package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class TriggerAbility implements CardAbility {
    private String triggerType;
    private boolean optional;
    private String condition;
    private JsonValue effect;

    public TriggerAbility(String triggerType, boolean optional, String condition, JsonValue effect) {
        this.triggerType = triggerType;
        this.optional = optional;
        this.condition = condition;
        this.effect = effect;
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

    public JsonValue getEffect() {
        return effect;
    }
}
