package com.gempukku.startrek.server.game.effect.play;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.ability.DilemmaEffect;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class PlayoutDilemmaEffect extends EffectSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private CardAbilitySystem cardAbilitySystem;
    private ServerEntityIdSystem serverEntityIdSystem;

    public PlayoutDilemmaEffect() {
        super("playoutDilemmaEffect");
    }

    @Override
    protected void processEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String memoryName = gameEffect.getDataString("memoryCard");
        String cardId = memory.getValue(memoryName);
        Entity cardEntity = serverEntityIdSystem.findfromId(cardId);
        DilemmaEffect dilemmaEffect = cardAbilitySystem.getCardAbilities(cardEntity, DilemmaEffect.class).get(0);
        Array<JsonValue> effects = dilemmaEffect.getEffects();
        int resolvedEffectIndex = Integer.parseInt(memory.getValue("effectIndex", "-1"));
        int nextEffectIndex = resolvedEffectIndex + 1;
        if (nextEffectIndex < effects.size) {
            JsonValue effect = effects.get(nextEffectIndex);
            stackEffect(createActionFromJson(effect, cardEntity));
            memory.setValue("effectIndex", String.valueOf(nextEffectIndex));
        } else {
            memory.removeValue("effectIndex");
            removeTopEffectFromStack();
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"memoryCard"},
                new String[]{});
    }
}
