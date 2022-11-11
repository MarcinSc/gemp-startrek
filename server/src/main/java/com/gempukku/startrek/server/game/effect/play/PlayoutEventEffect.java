package com.gempukku.startrek.server.game.effect.play;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.EventAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class PlayoutEventEffect extends EffectSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private CardAbilitySystem cardAbilitySystem;

    public PlayoutEventEffect() {
        super("playoutEventEffect");
    }

    @Override
    protected void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String memoryName = gameEffect.getDataString("memoryCard");
        int cardId = Integer.parseInt(memory.getValue(memoryName));
        Entity cardEntity = world.getEntity(cardId);
        EventAbility eventAbility = cardAbilitySystem.getCardAbilities(cardEntity, EventAbility.class).get(0);
        boolean costsPaid = Boolean.parseBoolean(memory.getValue("costsPaid", "false"));
        if (!costsPaid) {
            JsonValue costs = eventAbility.getCosts();
            int paidCostIndex = Integer.parseInt(memory.getValue("costIndex", "-1"));
            int nextCostIndex = paidCostIndex + 1;
            if (nextCostIndex < costs.size) {
                JsonValue cost = costs.get(nextCostIndex);
                stackEffect(createActionFromJson(cost, cardEntity));
                memory.setValue("costIndex", String.valueOf(nextCostIndex));
            } else {
                memory.setValue("costsPaid", "true");
                costsPaid = true;
            }
        }
        if (costsPaid) {
            JsonValue effects = eventAbility.getEffects();
            ;
            int resolvedEffectIndex = Integer.parseInt(memory.getValue("effectIndex", "-1"));
            int nextEffectIndex = resolvedEffectIndex + 1;
            if (nextEffectIndex < effects.size) {
                JsonValue effect = effects.get(nextEffectIndex);
                stackEffect(createActionFromJson(effect, cardEntity));
                memory.setValue("effectIndex", String.valueOf(nextEffectIndex));
            } else {
                memory.removeValue("costsPaid");
                memory.removeValue("costIndex");
                memory.removeValue("effectIndex");
                removeTopEffectFromStack();
            }
        }
    }
}
