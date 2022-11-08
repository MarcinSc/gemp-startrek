package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class SequenceEffect extends EffectSystem {
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;
    private ComponentMapper<EffectMemoryComponent> effectMemoryComponentMapper;

    public SequenceEffect() {
        super("sequence", "sequenceForPlayer");
    }

    @Override
    protected void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String effectType = gameEffect.getType();
        if (effectType.equals("sequence")) {
            sequence(sourceEntity, gameEffect, memory);
        } else if (effectType.equals("sequenceForPlayer")) {
            sequenceForPlayer(sourceEntity, gameEffect, memory);
        }
    }

    private void sequence(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        JsonValue action = gameEffect.getClonedDataObject("actions");
        String indexMemoryName = gameEffect.getDataString("memory", "stackedIndex");
        String stackedIndex = memory.getValue(indexMemoryName);
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            // Finished the effect - remove from stack
            removeTopEffectFromStack();
        } else {
            JsonValue actionToStack = action.get(nextActionIndex);
            Entity stackedEntity = createActionFromJson(actionToStack, sourceEntity);
            memory.setValue(indexMemoryName, String.valueOf(nextActionIndex));

            stackEffect(stackedEntity);
        }
    }

    private void sequenceForPlayer(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        JsonValue action = gameEffect.getClonedDataObject("actions");
        String indexMemoryName = gameEffect.getDataString("memory", "stackedIndex");
        String stackedIndex = memory.getValue(indexMemoryName);
        String player = gameEffect.getDataString("player");
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            // Finished the effect - remove from stack
            removeTopEffectFromStack();
        } else {
            JsonValue nextAction = action.get(nextActionIndex);
            nextAction.addChild("player", new JsonValue(player));
            Entity actionToStack = createActionFromJson(nextAction, sourceEntity);
            memory.setValue(indexMemoryName, String.valueOf(nextActionIndex));

            stackEffect(actionToStack);
        }
    }
}
