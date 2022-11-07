package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.game.amount.AmountResolverSystem;

public class LessOrEqualConditionHandler extends ConditionSystem {
    private AmountResolverSystem amountResolverSystem;

    public LessOrEqualConditionHandler() {
        super("lessOrEqual");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, ObjectMap<String, String> memory, Array<String> parameters) {
        int firstValue = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(0));
        int secondValue = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
        return firstValue <= secondValue;
    }
}
