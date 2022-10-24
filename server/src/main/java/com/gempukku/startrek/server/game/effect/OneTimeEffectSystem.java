package com.gempukku.startrek.server.game.effect;

import com.artemis.Entity;

public abstract class OneTimeEffectSystem extends EffectSystem {
    public OneTimeEffectSystem(String... effectTypes) {
        super(effectTypes);
    }

    @Override
    protected final void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        processOneTimeEffect(gameEffectEntity, gameEffect);
        removeEffectFromStack(gameEffectEntity);
    }

    protected abstract void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect);
}
