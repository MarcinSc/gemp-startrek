package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.CardAbility;

public class DilemmaEffect implements CardAbility {
    private JsonValue effect;

    public DilemmaEffect(JsonValue effect) {
        this.effect = effect;
    }

    public JsonValue getEffect() {
        return effect;
    }
}
