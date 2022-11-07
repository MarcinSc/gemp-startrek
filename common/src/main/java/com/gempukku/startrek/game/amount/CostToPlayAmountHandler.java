package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.card.CardLookupSystem;

public class CostToPlayAmountHandler extends AmountSystem {
    private CardLookupSystem cardLookupSystem;

    public CostToPlayAmountHandler() {
        super("costToPlay");
    }

    @Override
    public int resolveAmount(String type, Entity sourceEntity, ObjectMap<String, String> memory, Array<String> parameters) {
        return cardLookupSystem.getCardDefinition(sourceEntity).getCost();
    }
}
