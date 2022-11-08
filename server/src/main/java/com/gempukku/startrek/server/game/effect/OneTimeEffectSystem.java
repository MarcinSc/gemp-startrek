package com.gempukku.startrek.server.game.effect;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;

public abstract class OneTimeEffectSystem extends EffectSystem {
    public OneTimeEffectSystem(String... effectTypes) {
        super(effectTypes);
    }

    @Override
    protected final void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, Memory memory) {
        processOneTimeEffect(gameEffectEntity, gameEffect, memory);
        removeEffectFromStack(gameEffectEntity);
    }

    protected abstract void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, Memory memory);
}
