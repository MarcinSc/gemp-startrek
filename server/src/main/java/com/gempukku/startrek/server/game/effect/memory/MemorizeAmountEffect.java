package com.gempukku.startrek.server.game.effect.memory;

import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class MemorizeAmountEffect extends OneTimeEffectSystem {
    private AmountResolverSystem amountResolverSystem;

    public MemorizeAmountEffect() {
        super("memorizeAmount");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String memoryName = gameEffect.getDataString("memory");
        String amount = gameEffect.getDataString("amount");

        int result = amountResolverSystem.resolveAmount(sourceEntity, memory, amount);
        memory.setValue(memoryName, String.valueOf(result));
    }
}
