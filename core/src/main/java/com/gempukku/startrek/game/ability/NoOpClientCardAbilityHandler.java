package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class NoOpClientCardAbilityHandler extends ClientCardAbilityHandlerSystem {
    public NoOpClientCardAbilityHandler() {
        super("trigger", "dilemmaEffect");
    }

    @Override
    public ClientCardAbility resolveClientCardAbility(JsonValue cardAbility) {
        return null;
    }
}
