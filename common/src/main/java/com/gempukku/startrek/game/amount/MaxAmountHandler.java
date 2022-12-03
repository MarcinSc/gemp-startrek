package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class MaxAmountHandler extends AmountSystem {
    private AmountResolverSystem amountResolverSystem;

    public MaxAmountHandler() {
        super("max");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        int result = Integer.MIN_VALUE;
        for (String parameter : parameters) {
            int value = amountResolverSystem.resolveAmount(sourceEntity, memory, parameter);
            result = Math.max(result, value);
        }

        return result;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
    }
}
