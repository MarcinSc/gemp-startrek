package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.ability.CardAbilityHandlerSystem;

public class TriggerAbilityHandler extends CardAbilityHandlerSystem {
    public TriggerAbilityHandler() {
        super("trigger");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        boolean optional = cardAbility.getBoolean("optional", false);
        String condition = cardAbility.getString("condition");
        JsonValue effect = cardAbility.get("effect");
        return new TriggerAbility(optional, condition, effect);
    }
}
