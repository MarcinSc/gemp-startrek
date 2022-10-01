package com.gempukku.startrek.server.game.amount;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

public class AmountResolverSystem extends BaseSystem {
    public int resolveAmount(JsonValue value) {
        if (value.type() == JsonValue.ValueType.longValue)
            return value.asInt();
        throw new RuntimeException("Unable to resolve amount: " + value.toJson(JsonWriter.OutputType.json));
    }

    @Override
    protected void processSystem() {

    }
}
