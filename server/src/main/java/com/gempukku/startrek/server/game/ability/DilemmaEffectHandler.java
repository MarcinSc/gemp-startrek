package com.gempukku.startrek.server.game.ability;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public class DilemmaEffectHandler extends ServerCardAbilityHandlerSystem {
    private GameEffectSystem gameEffectSystem;

    public DilemmaEffectHandler() {
        super("dilemmaEffect");
    }

    @Override
    public CardAbility resolveCardAbility(JsonValue cardAbility) {
        return new DilemmaEffect(cardAbility.get("effects"));
    }

    @Override
    public void validateAbility(JsonValue cardAbility) {
        ValidateUtil.abilityExpectedFields(cardAbility,
                new String[]{"effects"},
                new String[]{});
        JsonValue effects = cardAbility.get("effects");
        validateEffects(effects);
    }
}
