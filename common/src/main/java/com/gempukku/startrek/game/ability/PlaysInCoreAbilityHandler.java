package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;

public class PlaysInCoreAbilityHandler extends CardAbilityHandlerSystem {
    public PlaysInCoreAbilityHandler() {
        super("playsInCore");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        return new PlaysInCoreAbility();
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{},
                new String[]{});
    }
}
