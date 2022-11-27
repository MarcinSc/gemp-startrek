package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class MinusAmountHandler extends AmountSystem {
    private AmountResolverSystem amountResolverSystem;

    public MinusAmountHandler() {
        super("minus");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        return amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(0))
                - amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 2);
        amountResolverSystem.validateAmount(parameters.get(0));
        amountResolverSystem.validateAmount(parameters.get(1));
    }
}
