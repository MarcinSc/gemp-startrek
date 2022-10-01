package com.gempukku.startrek.server.game.effect;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;

public class StackActionEffect extends EffectSystem {
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public StackActionEffect() {
        super("stackAction");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        JsonValue action = gameEffect.getData().get("action");
        String stackedIndex = gameEffect.getMemory().get("stackedIndex");
        int nextActionIndex = 0;
        if (stackedIndex != null) {
            nextActionIndex = Integer.parseInt(stackedIndex) + 1;
        }

        if (nextActionIndex == action.size) {
            // Finished the effect - remove from stack
            removeEffectFromStack(gameEffectEntity);
        } else {
            JsonValue actionToStack = getArrayElement(action, nextActionIndex);
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            newGameEffect.setType(actionToStack.getString("type"));
            newGameEffect.setData(actionToStack);
            gameEffect.getMemory().put("stackedIndex", String.valueOf(nextActionIndex));

            stackEffect(stackedEntity);
        }
    }

    private JsonValue getArrayElement(JsonValue arrayOrObject, int index) {
        if (arrayOrObject.type() == JsonValue.ValueType.object) {
            if (index > 0)
                throw new ArrayIndexOutOfBoundsException(index);
            return arrayOrObject;
        } else if (arrayOrObject.type() == JsonValue.ValueType.array) {
            return arrayOrObject.get(index);
        } else {
            throw new RuntimeException("Passed element is not object or array");
        }
    }
}
