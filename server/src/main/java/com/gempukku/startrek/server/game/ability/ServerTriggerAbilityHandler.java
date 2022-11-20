package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.ability.CardAbilityHandlerSystem;

public class ServerTriggerAbilityHandler extends CardAbilityHandlerSystem {
    public ServerTriggerAbilityHandler() {
        super("trigger");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String triggerType = cardAbility.getString("triggerType");
        boolean optional = cardAbility.getBoolean("optional", false);
        String condition = cardAbility.getString("condition");
        JsonValue cost = cardAbility.get("cost");
        JsonValue effect = cardAbility.get("effect");
        return new ServerTriggerAbility(triggerType, optional, condition, cost, effect);
    }
}
