package com.gempukku.startrek.server.game.effect;

import com.artemis.Entity;

public interface GameEffectHandler {
    boolean processEndingEffect(Entity gameEffectEntity, GameEffectComponent gameEffect);
}
