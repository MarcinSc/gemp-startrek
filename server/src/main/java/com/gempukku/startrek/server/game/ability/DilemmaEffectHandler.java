package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.ability.CardAbilityHandlerSystem;

public class DilemmaEffectHandler extends CardAbilityHandlerSystem {
    public DilemmaEffectHandler() {
        super("dilemmaEffect");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        return new DilemmaEffect(cardAbility.get("effect"));
    }
}
