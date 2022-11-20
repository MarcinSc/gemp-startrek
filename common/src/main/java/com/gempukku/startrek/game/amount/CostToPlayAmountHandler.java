package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class CostToPlayAmountHandler extends AmountSystem {
    private CardLookupSystem cardLookupSystem;

    public CostToPlayAmountHandler() {
        super("costToPlay");
    }

    @Override
    public int resolveAmount(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        return cardLookupSystem.getCardDefinition(sourceEntity).getCost();
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
