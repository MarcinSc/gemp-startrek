package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.CardInPlayComponent;

public class ShipRangeAmountHandler extends AmountSystem {
    private CardLookupSystem cardLookupSystem;

    public ShipRangeAmountHandler() {
        super("shipRange");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        CardDefinition shipDefinition = cardLookupSystem.getCardDefinition(sourceEntity);
        int usedRanged = 0;
        CardInPlayComponent cardInPlay = sourceEntity.getComponent(CardInPlayComponent.class);
        if (cardInPlay != null)
            usedRanged += cardInPlay.getRangeUsed();
        return shipDefinition.getRange() - usedRanged;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
