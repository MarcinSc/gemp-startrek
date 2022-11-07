package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.server.JsonValueHandler;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class SequenceEffect extends EffectSystem {
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public SequenceEffect() {
        super("sequence", "sequenceForPlayer");
    }

    @Override
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String effectType = gameEffect.getType();
        if (effectType.equals("sequence")) {
            sequence(gameEffectEntity, gameEffect);
        } else if (effectType.equals("sequenceForPlayer")) {
            sequenceForPlayer(gameEffectEntity, gameEffect);
        }
    }

    private void sequence(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        JsonValue action = gameEffect.getData().get("actions");
        String stackedIndex = gameEffect.getMemory().get("stackedIndex");
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            // Finished the effect - remove from stack
            removeEffectFromStack(gameEffectEntity);
        } else {
            JsonValue actionToStack = JsonValueHandler.clone(action.get(nextActionIndex));
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            newGameEffect.setType(actionToStack.getString("type"));
            newGameEffect.setData(actionToStack);
            gameEffect.getMemory().put("stackedIndex", String.valueOf(nextActionIndex));

            stackEffect(stackedEntity);
        }
    }

    private void sequenceForPlayer(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        JsonValue action = gameEffect.getData().get("actions");
        String stackedIndex = gameEffect.getMemory().get("stackedIndex");
        String player = gameEffect.getData().getString("player");
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            // Finished the effect - remove from stack
            removeEffectFromStack(gameEffectEntity);
        } else {
            JsonValue actionToStack = JsonValueHandler.clone(action.get(nextActionIndex));
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            actionToStack.addChild("player", new JsonValue(player));
            newGameEffect.setType(actionToStack.getString("type"));
            newGameEffect.setData(actionToStack);
            gameEffect.getMemory().put("stackedIndex", String.valueOf(nextActionIndex));

            stackEffect(stackedEntity);
        }
    }
}
