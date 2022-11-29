package com.gempukku.startrek.server.game.effect.memory;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class MemorizeStringEffect extends OneTimeEffectSystem {
    private AmountResolverSystem amountResolverSystem;

    public MemorizeStringEffect() {
        super("memorizeString");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String memoryName = gameEffect.getDataString("memory");
        memory.setValue(memoryName, gameEffect.getDataString("value"));
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"memory", "value"},
                new String[]{});
    }
}
