package com.gempukku.startrek.server.game.effect.control;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class ConditionEffect extends EffectSystem {
    private ConditionResolverSystem conditionResolverSystem;

    public ConditionEffect() {
        super("condition");
    }

    @Override
    protected void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String condition = gameEffect.getDataString("condition");
        JsonValue action;
        if (conditionResolverSystem.resolveBoolean(sourceEntity, memory, condition)) {
            action = gameEffect.getClonedDataObject("trueEffect");
        } else {
            action = gameEffect.getClonedDataObject("falseEffect");
        }
        removeTopEffectFromStack();
        if (action != null) {
            Entity entityToStack = createActionFromJson(action, sourceEntity);
            stackEffect(entityToStack);
        }
    }
}
