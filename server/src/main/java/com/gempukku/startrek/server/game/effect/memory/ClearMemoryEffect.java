package com.gempukku.startrek.server.game.effect.memory;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class ClearMemoryEffect extends OneTimeEffectSystem {
    public ClearMemoryEffect() {
        super("clearMemory");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String memoryList = gameEffect.getDataString("memory");
        String[] memories = memoryList.split(",");
        for (String memoryName : memories) {
            memory.removeValue(memoryName);
        }
    }
}
