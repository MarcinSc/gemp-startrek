package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.CardAbility;

public class ServerInterruptAbility implements CardAbility {
    private String condition;
    private JsonValue costs;
    private JsonValue effects;

    public ServerInterruptAbility(String condition, JsonValue costs, JsonValue effects) {
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
