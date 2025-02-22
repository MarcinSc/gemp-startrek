package com.gempukku.startrek.server.game.effect.memory;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class ClearMemoryEffect extends OneTimeEffectSystem {
    public ClearMemoryEffect() {
        super("clearMemory");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String memoryList = gameEffect.getDataString("memory");
        String[] memories = StringUtils.split(memoryList);
        for (String memoryName : memories) {
            memory.removeValue(memoryName);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"memory"},
                new String[]{});
    }
}
