package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.CardAbility;

public class TriggerAbility implements CardAbility {
    private boolean optional;
    private String condition;
    private JsonValue effect;

    public TriggerAbility(boolean optional, String condition, JsonValue effect) {
        this.optional = optional;
        this.condition = condition;
        this.effect = effect;
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
