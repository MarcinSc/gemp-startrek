package com.gempukku.startrek.game.ability;

import com.badlogic.gdx.utils.JsonValue;

public class ClientTriggerAbilityHandler extends CardAbilityHandlerSystem {
    public ClientTriggerAbilityHandler() {
        super("trigger");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String triggerType = cardAbility.getString("triggerType");
        boolean optional = cardAbility.getBoolean("optional", false);
        String condition = cardAbility.getString("condition");
        return new TriggerAbility(triggerType, optional, condition);
    }
}
