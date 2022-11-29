package com.gempukku.startrek.server.game.effect.play;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.server.game.ability.ServerTriggerAbility;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class PlayoutTriggerEffect extends EffectSystem {
    private CardAbilitySystem cardAbilitySystem;
    private ServerEntityIdSystem serverEntityIdSystem;

    public PlayoutTriggerEffect() {
        super("playoutTrigger");
    }

    @Override
    protected void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        String memoryName = gameEffect.getDataString("cardMemory");
        String cardId = memory.getValue(memoryName);
        Entity cardEntity = serverEntityIdSystem.findfromId(cardId);
        String triggerIndexMemory = gameEffect.getDataString("triggerIndexMemory");
        int triggerIndex = Integer.parseInt(memory.getValue(triggerIndexMemory));

        ServerTriggerAbility serverTriggerAbility = (ServerTriggerAbility) cardAbilitySystem.getCardAbilities(cardEntity).get(triggerIndex);
        boolean costsPaid = Boolean.parseBoolean(memory.getValue("costsPaid", "false"));
        if (!costsPaid) {
            Array<JsonValue> costs = serverTriggerAbility.getCosts();
            int paidCostIndex = Integer.parseInt(memory.getValue("costIndex", "-1"));
            int nextCostIndex = paidCostIndex + 1;
            if (nextCostIndex < costs.size) {
                JsonValue cost = costs.get(nextCostIndex);
                Entity actionFromJson = createActionFromJson(cost, cardEntity);
                stackEffect(actionFromJson);
                memory.setValue("costIndex", String.valueOf(nextCostIndex));
            } else {
                memory.setValue("costsPaid", "true");
                costsPaid = true;
            }
        }
        if (costsPaid) {
            Array<JsonValue> effects = serverTriggerAbility.getEffects();
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

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"cardMemory", "triggerIndexMemory"},
                new String[]{});

    }
}
