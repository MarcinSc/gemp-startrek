package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class NoOpClientCardAbilityHandler extends CardAbilityHandlerSystem {
    public NoOpClientCardAbilityHandler() {
        super("dilemmaEffect");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        return null;
    }
}
