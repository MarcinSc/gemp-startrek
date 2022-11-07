package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public interface CardAbilityHandler {
    CardAbility resolveCardAbility(JsonValue cardAbility);
}
