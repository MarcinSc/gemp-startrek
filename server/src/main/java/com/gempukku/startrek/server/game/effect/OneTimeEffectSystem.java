package com.gempukku.startrek.server.game.effect;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;

public abstract class OneTimeEffectSystem extends EffectSystem {
    public OneTimeEffectSystem(String... effectTypes) {
        super(effectTypes);
    }

    @Override
    protected final void processEffect(Entity sourceEntity, Entity effectEntity, GameEffectComponent gameEffect, Memory memory) {
        processOneTimeEffect(sourceEntity, effectEntity, gameEffect, memory);
        removeTopEffectFromStack();
    }

    protected abstract void processOneTimeEffect(Entity sourceEntity, Entity effectEntity, GameEffectComponent gameEffect, Memory memory);
}
