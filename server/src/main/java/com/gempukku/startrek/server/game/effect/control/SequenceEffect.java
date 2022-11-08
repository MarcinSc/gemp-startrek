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
    protected void processEffect(Entity sourceEntity, Entity effectEntity, GameEffectComponent gameEffect, Memory memory) {
        String effectType = gameEffect.getType();
        if (effectType.equals("sequence")) {
            sequence(effectEntity, gameEffect, memory);
        } else if (effectType.equals("sequenceForPlayer")) {
            sequenceForPlayer(effectEntity, gameEffect, memory);
        }
    }

    private void sequence(Entity effectEntity, GameEffectComponent gameEffect, Memory memory) {
        JsonValue action = gameEffect.getClonedDataObject("actions");
        String stackedIndex = memory.getValue("stackedIndex");
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            // Finished the effect - remove from stack
            removeTopEffectFromStack();
        } else {
            JsonValue actionToStack = action.get(nextActionIndex);
            String actionType = actionToStack.getString("type");
            boolean createMemory = actionToStack.getBoolean("memory", false);
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            if (createMemory) {
                effectMemoryComponentMapper.create(stackedEntity).setMemoryType("action - " + actionType);
            }
            newGameEffect.setType(actionType);
            newGameEffect.setData(actionToStack);
            memory.setValue("stackedIndex", String.valueOf(nextActionIndex));

            stackEffect(stackedEntity);
        }
    }

    private void sequenceForPlayer(Entity effectEntity, GameEffectComponent gameEffect, Memory memory) {
        JsonValue action = gameEffect.getClonedDataObject("actions");
        String stackedIndex = memory.getValue("stackedIndex");
        String player = gameEffect.getDataString("player");
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            // Finished the effect - remove from stack
            removeTopEffectFromStack();
        } else {
            JsonValue actionToStack = action.get(nextActionIndex);
            String actionType = actionToStack.getString("type");
            boolean createMemory = actionToStack.getBoolean("memory", false);
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            if (createMemory) {
                effectMemoryComponentMapper.create(stackedEntity).setMemoryType("action - " + actionType);
            }
            actionToStack.addChild("player", new JsonValue(player));
            newGameEffect.setType(actionType);
            newGameEffect.setData(actionToStack);
            memory.setValue("stackedIndex", String.valueOf(nextActionIndex));

            stackEffect(stackedEntity);
        }
    }
}
