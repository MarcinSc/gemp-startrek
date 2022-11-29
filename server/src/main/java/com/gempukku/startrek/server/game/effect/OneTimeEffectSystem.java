package com.gempukku.startrek.server.game.effect;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;

public abstract class OneTimeEffectSystem extends EffectSystem {
    public OneTimeEffectSystem(String... effectTypes) {
        super(effectTypes);
    }

    @Override
    protected final void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        processOneTimeEffect(sourceEntity, memory, gameEffect);
        removeTopEffectFromStack();
    }

    protected abstract void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect);
}
