package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

import java.util.function.Consumer;

public class CostToPlayAmountHandler extends AmountSystem {
    private CardLookupSystem cardLookupSystem;
    private CardFilteringSystem cardFilteringSystem;

    public CostToPlayAmountHandler() {
        super("costToPlay");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        int[] total = new int[1];
        cardFilteringSystem.forEachCard(sourceEntity, memory,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        total[0] += cardLookupSystem.getCardDefinition(entity).getCost();
                    }
                }, cardFilteringSystem.createAndFilter(parameters));
        return total[0];
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        for (String parameter : parameters) {
            cardFilteringSystem.validateFilter(parameter);
        }
    }
}
