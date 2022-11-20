package com.gempukku.startrek.server.game.effect;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;

public interface GameEffectHandler {
    boolean processEndingEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory);

    void validate(JsonValue effect);
}
