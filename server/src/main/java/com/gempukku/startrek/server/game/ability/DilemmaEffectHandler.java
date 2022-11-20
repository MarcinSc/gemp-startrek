package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.ability.CardAbilityHandlerSystem;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public class DilemmaEffectHandler extends CardAbilityHandlerSystem {
    private GameEffectSystem gameEffectSystem;

    public DilemmaEffectHandler() {
        super("dilemmaEffect");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        return new DilemmaEffect(cardAbility.get("effect"));
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{"effect"},
                new String[]{});
        for (JsonValue child : cardAbility.get("effect")) {
            gameEffectSystem.validate(child);
        }
    }
}
