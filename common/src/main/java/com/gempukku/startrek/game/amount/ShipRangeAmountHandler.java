package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.CardInPlayComponent;

public class ShipRangeAmountHandler extends AmountSystem {
    private CardLookupSystem cardLookupSystem;
    private AmountResolverSystem amountResolverSystem;

    public ShipRangeAmountHandler() {
        super("shipRange");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        int cardRange = amountResolverSystem.resolveAmount(sourceEntity, memory, "attribute(Range)");
        int usedRanged = 0;
        CardInPlayComponent cardInPlay = sourceEntity.getComponent(CardInPlayComponent.class);
        if (cardInPlay != null)
            usedRanged += cardInPlay.getRangeUsed();
        return cardRange - usedRanged;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
