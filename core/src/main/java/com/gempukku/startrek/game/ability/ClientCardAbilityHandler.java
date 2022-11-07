package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public interface ClientCardAbilityHandler {
    ClientCardAbility resolveClientCardAbility(JsonValue cardAbility);
}
