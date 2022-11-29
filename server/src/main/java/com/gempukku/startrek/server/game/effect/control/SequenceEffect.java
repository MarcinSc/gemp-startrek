package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public class SequenceEffect extends EffectSystem {
    private GameEffectSystem gameEffectSystem;
    private IdProviderSystem idProviderSystem;

    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;
    private ComponentMapper<EffectMemoryComponent> effectMemoryComponentMapper;

    public SequenceEffect() {
        super("sequence");
    }

    @Override
    protected void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        JsonValue action = gameEffect.getClonedDataObject("actions");
        String indexMemoryName = "sequenceIndex." + idProviderSystem.getEntityId(effectEntity);
        String stackedIndex = memory.getValue(indexMemoryName);
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            memory.removeValue(indexMemoryName);
            // Finished the effect - remove from stack
            removeTopEffectFromStack();
        } else {
            JsonValue actionToStack = action.get(nextActionIndex);
            Entity stackedEntity = createActionFromJson(actionToStack, sourceEntity);
            memory.setValue(indexMemoryName, String.valueOf(nextActionIndex));

            stackEffect(stackedEntity);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"actions"},
                new String[]{});
        validateEffects(effect.get("actions"));
    }
}
