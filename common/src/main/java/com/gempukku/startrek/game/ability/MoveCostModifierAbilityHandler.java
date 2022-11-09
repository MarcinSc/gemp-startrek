package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class MoveCostModifierAbilityHandler extends CardAbilityHandlerSystem {
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
}
