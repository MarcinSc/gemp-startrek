package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class CountAtLeastConditionHandler extends ConditionSystem {
    private CardFilteringSystem cardFilteringSystem;
    private AmountResolverSystem amountResolverSystem;

    public CountAtLeastConditionHandler() {
        super("countAtLeast");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        CardFilter filter = cardFilteringSystem.resolveCardFilter(parameters.get(0));
        int amount = amountResolverSystem.resolveAmount(sourceEntity, memory, parameters.get(1));
        return cardFilteringSystem.hasMatchingInPlay(sourceEntity, memory, filter, amount);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 2);
        cardFilteringSystem.validateFilter(parameters.get(0));
        amountResolverSystem.validateAmount(parameters.get(1));
    }
}
