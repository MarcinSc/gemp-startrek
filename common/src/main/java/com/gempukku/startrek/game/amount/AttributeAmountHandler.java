package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardAttribute;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class AttributeAmountHandler extends AmountSystem {
    private CardLookupSystem cardLookupSystem;

    public AttributeAmountHandler() {
        super("attribute");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        CardAttribute cardAttribute = CardAttribute.valueOf(parameters.get(0));
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(sourceEntity);

        switch (cardAttribute) {
            case Integrity:
                return cardDefinition.getIntegrity();
            case Cunning:
                return cardDefinition.getCunning();
            case Strength:
                return cardDefinition.getStrength();
            case Range:
                return cardDefinition.getRange();
            case Weapons:
                return cardDefinition.getWeapons();
            case Shields:
                return cardDefinition.getShields();
        }

        return 0;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        CardAttribute.valueOf(parameters.get(0));
    }
}
