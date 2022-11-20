package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.OrderAbility;

public class ServerOrderAbility extends OrderAbility {
    private Array<JsonValue> costs;
    private Array<JsonValue> effects;

    public ServerOrderAbility(String condition, JsonValue costs, JsonValue effects) {
        super(condition);
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

    public Array<JsonValue> getCosts() {
        return costs;
    }

    public Array<JsonValue> getEffects() {
        return effects;
    }
}
