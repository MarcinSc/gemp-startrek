package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class RemoveEffectFromStackEffect extends OneTimeEffectSystem {
    private ZoneOperations zoneOperations;

    public RemoveEffectFromStackEffect() {
        super("removeEffectFromStack");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        zoneOperations.removeEffectFromStack();
    }
}
