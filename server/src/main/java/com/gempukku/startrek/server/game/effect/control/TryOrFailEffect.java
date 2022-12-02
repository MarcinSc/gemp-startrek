package com.gempukku.startrek.server.game.effect.control;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class TryOrFailEffect extends EffectSystem {
    private ConditionResolverSystem conditionResolverSystem;

    public TryOrFailEffect() {
        super("tryOrFail");
    }

    @Override
    protected void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        String stepMemoryName = getMemoryName(effectEntity, "step");

        String step = memory.getValue(stepMemoryName);
        if (step == null) {
            // Check condition, if should go directly to failure
            String condition = gameEffect.getDataString("condition");
            if (conditionResolverSystem.resolveBoolean(sourceEntity, memory, condition)) {
                JsonValue action = gameEffect.getClonedDataObject("tryEffect");
                Entity entityToStack = createActionFromJson(action, sourceEntity);
                stackEffect(entityToStack);
                memory.setValue(stepMemoryName, "stackedTryEffect");
            } else {
                JsonValue action = gameEffect.getClonedDataObject("failEffect");
                Entity entityToStack = createActionFromJson(action, sourceEntity);
                stackEffect(entityToStack);
                memory.setValue(stepMemoryName, "stackedFailEffect");
            }
        } else if (step.equals("stackedTryEffect")) {
            // TODO check if the effect was successfully completed, if not - stack "failEffect"
            memory.removeValue(stepMemoryName);
            removeTopEffectFromStack();
        } else if (step.equals("stackedFailEffect")) {
            memory.removeValue(stepMemoryName);
            removeTopEffectFromStack();
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"condition", "tryEffect", "failEffect"},
                new String[]{});
        conditionResolverSystem.validateCondition(effect.getString("condition"));
        validateOneEffect(effect.get("tryEffect"));
        validateOneEffect(effect.get("failEffect"));
    }
}
