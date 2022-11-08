package com.gempukku.startrek.server.game.effect;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;

public interface GameEffectHandler {
    boolean processEndingEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, Memory memory);
}
