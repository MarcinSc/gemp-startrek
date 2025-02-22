package com.gempukku.startrek.server.game.effect.control;

import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public class ConditionEffect extends EffectSystem {
    private GameEffectSystem gameEffectSystem;
    private ConditionResolverSystem conditionResolverSystem;

    public ConditionEffect() {
        super("condition");
    }

    @Override
    protected void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        String conditionMemory = getMemoryName(effectEntity, "effectStacked");
        String effectStacked = memory.getValue(conditionMemory);
        if (effectStacked != null && effectStacked.equals("true")) {
            memory.removeValue(conditionMemory);
            removeTopEffectFromStack();
        } else {
            String condition = gameEffect.getDataString("condition");
            JsonValue action;
            if (conditionResolverSystem.resolveBoolean(sourceEntity, memory, condition)) {
                action = gameEffect.getClonedDataObject("trueEffect");
            } else {
                action = gameEffect.getClonedDataObject("falseEffect");
            }
            if (action != null) {
                Entity entityToStack = createActionFromJson(action, sourceEntity);
                stackEffect(entityToStack);
            }
            memory.setValue(conditionMemory, "true");
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"condition"},
                new String[]{"trueEffect", "falseEffect"});
        conditionResolverSystem.validateCondition(effect.getString("condition"));
        JsonValue trueEffect = effect.get("trueEffect");
        if (trueEffect != null)
            validateOneEffect(trueEffect);
        JsonValue falseEffect = effect.get("falseEffect");
        if (falseEffect != null)
            validateOneEffect(falseEffect);
    }
}
