package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.CardAbilityHandlerSystem;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public abstract class ServerCardAbilityHandlerSystem extends CardAbilityHandlerSystem {
    private GameEffectSystem gameEffectSystem;

    public ServerCardAbilityHandlerSystem(String... abilityTypes) {
        super(abilityTypes);
    }

    protected void validateEffects(JsonValue effects) {
        if (effects.type() == JsonValue.ValueType.object)
            gameEffectSystem.validate(effects);
        else {
            for (JsonValue effect : effects) {
                gameEffectSystem.validate(effect);
            }
        }
    }
}
