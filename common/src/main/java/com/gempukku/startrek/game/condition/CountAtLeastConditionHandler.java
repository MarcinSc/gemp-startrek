package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class CountAtLeastConditionHandler extends ConditionSystem {
    private CardFilteringSystem cardFilteringSystem;
    private AmountResolverSystem amountResolverSystem;

    public CountAtLeastConditionHandler() {
        super("countAtLeast");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        String from = parameters.get(0);
        String filter = parameters.get(1);
        int amount = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(2));
        return cardFilteringSystem.hasCardCount(sourceEntity, memory, from, amount, filter);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 3);
        cardFilteringSystem.validateSource(parameters.get(0));
        cardFilteringSystem.validateFilter(parameters.get(1));
        amountResolverSystem.validateAmount(parameters.get(2));
    }
}
