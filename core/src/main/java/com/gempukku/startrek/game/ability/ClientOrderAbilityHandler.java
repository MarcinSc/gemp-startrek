package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class ClientOrderAbilityHandler extends CardAbilityHandlerSystem {
    public ClientOrderAbilityHandler() {
        super("order");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String condition = cardAbility.getString("condition");
        return new OrderAbility(condition);
    }
}
