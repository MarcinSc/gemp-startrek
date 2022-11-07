package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
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
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        String effectType = gameEffect.getType();
        if (effectType.equals("sequence")) {
            sequence(gameEffectEntity, gameEffect, memory);
        } else if (effectType.equals("sequenceForPlayer")) {
            sequenceForPlayer(gameEffectEntity, gameEffect, memory);
        }
    }

    private void sequence(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        JsonValue action = gameEffect.getClonedDataObject("actions");
        String stackedIndex = memory.get("stackedIndex");
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            // Finished the effect - remove from stack
            removeEffectFromStack(gameEffectEntity);
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
            memory.put("stackedIndex", String.valueOf(nextActionIndex));

            stackEffect(stackedEntity);
        }
    }

    private void sequenceForPlayer(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        JsonValue action = gameEffect.getClonedDataObject("actions");
        String stackedIndex = memory.get("stackedIndex");
        String player = gameEffect.getDataString("player");
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            // Finished the effect - remove from stack
            removeEffectFromStack(gameEffectEntity);
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
            memory.put("stackedIndex", String.valueOf(nextActionIndex));

            stackEffect(stackedEntity);
        }
    }
}
