package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class PlaysInCoreAbilityHandler extends CardAbilityHandlerSystem {
    public PlaysInCoreAbilityHandler() {
        super("playsInCore");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        return new PlaysInCoreAbility();
    }
}
