package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;

public class EqualsConditionHandler extends ConditionSystem {
    private AmountResolverSystem amountResolverSystem;

    public EqualsConditionHandler() {
        super("equals");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        int firstValue = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(0));
        int secondValue = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
        return firstValue == secondValue;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 2);
        amountResolverSystem.validateAmount(parameters.get(0));
        amountResolverSystem.validateAmount(parameters.get(1));
    }
}
