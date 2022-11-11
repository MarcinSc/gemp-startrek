package com.gempukku.startrek.server.game.effect.play;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class PlayoutTriggerEffect extends EffectSystem {
    public PlayoutTriggerEffect() {
        super("playoutTrigger");
    }

    @Override
    protected void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {

    }
}
