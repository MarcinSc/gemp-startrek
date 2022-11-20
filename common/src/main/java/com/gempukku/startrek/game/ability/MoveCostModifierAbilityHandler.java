package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;

public class MoveCostModifierAbilityHandler extends CardAbilityHandlerSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private AmountResolverSystem amountResolverSystem;

    public MoveCostModifierAbilityHandler() {
        super("moveCostModifier");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String shipFilter = cardAbility.getString("shipFilter", "any");
        String fromFilter = cardAbility.getString("fromFilter", "any");
        String toFilter = cardAbility.getString("toFilter", "any");
        String amount = cardAbility.getString("amount");
        return new MoveCostModifier(shipFilter, fromFilter, toFilter, amount);
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{},
                new String[]{"shipFilter", "fromFilter", "toFilter", "amount"});
        String shipFilter = cardAbility.getString("shipFilter", "any");
        String fromFilter = cardAbility.getString("fromFilter", "any");
        String toFilter = cardAbility.getString("toFilter", "any");
        String amount = cardAbility.getString("amount");

        cardFilterResolverSystem.validate(shipFilter);
        cardFilterResolverSystem.validate(fromFilter);
        cardFilterResolverSystem.validate(toFilter);
        amountResolverSystem.validateAmount(amount);
    }
}
