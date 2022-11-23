package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class MoveCostModifierAbilityHandler extends CardAbilityHandlerSystem {
    private CardFilteringSystem cardFilteringSystem;
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

        cardFilteringSystem.validateFilter(shipFilter);
        cardFilteringSystem.validateFilter(fromFilter);
        cardFilteringSystem.validateFilter(toFilter);
        amountResolverSystem.validateAmount(amount);
    }
}
