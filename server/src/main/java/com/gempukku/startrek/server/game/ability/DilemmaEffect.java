package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.CardAbility;

public class DilemmaEffect implements CardAbility {
    private Array<JsonValue> effects;

    public DilemmaEffect(JsonValue effects) {
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

    public Array<JsonValue> getEffects() {
        return effects;
    }
}
