package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.ability.CardAbilityHandlerSystem;

public class ServerOrderAbilityHandler extends CardAbilityHandlerSystem {
    public ServerOrderAbilityHandler() {
        super("order");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        String condition = cardAbility.getString("condition", null);
        JsonValue costs = cardAbility.get("costs");
        JsonValue effects = cardAbility.get("effects");
        return new ServerOrderAbility(condition, costs, effects);
    }
}
