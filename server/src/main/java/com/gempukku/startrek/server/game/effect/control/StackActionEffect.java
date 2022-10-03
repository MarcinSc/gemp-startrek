package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class StackActionEffect extends EffectSystem {
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public StackActionEffect() {
        super("stackAction", "stackForEachPlayer");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String effectType = gameEffect.getType();
        if (effectType.equals("stackAction")) {
            stackActionEffect(gameEffectEntity, gameEffect);
        } else if (effectType.equals("stackForEachPlayer")) {
            stackForEachPlayerEffect(gameEffectEntity, gameEffect);
        }
    }

    private void stackActionEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
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

    private void stackForEachPlayerEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        GameComponent game = LazyEntityUtil.findEntityWithComponent(world, GameComponent.class).getComponent(GameComponent.class);
        Array<String> players = game.getPlayers();

        String playerIndex = gameEffect.getMemory().get("playerIndex");
        int executePlayerIndex = 0;
        if (playerIndex != null) {
            executePlayerIndex = Integer.parseInt(playerIndex);
        }

        if (executePlayerIndex == players.size) {
            // Finished all players - remove from stack
            removeEffectFromStack(gameEffectEntity);
        } else {
            String player = players.get(executePlayerIndex);
            JsonValue action = gameEffect.getData().get("action");
            String stackedIndex = gameEffect.getMemory().get("stackedIndex");
            int nextActionIndex = 0;
            if (stackedIndex != null) {
                nextActionIndex = Integer.parseInt(stackedIndex) + 1;
            }

            if (nextActionIndex == action.size) {
                // Finished the effect - increment player index
                gameEffect.getMemory().put("playerIndex", String.valueOf(executePlayerIndex + 1));
                gameEffect.getMemory().remove("stackedIndex");
            } else {
                JsonValue actionToStack = getArrayElement(action, nextActionIndex);
                actionToStack.remove("player");
                actionToStack.addChild("player", new JsonValue("username(" + player + ")"));
                Entity stackedEntity = world.createEntity();
                GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
                newGameEffect.setType(actionToStack.getString("type"));
                newGameEffect.setData(actionToStack);
                gameEffect.getMemory().put("stackedIndex", String.valueOf(nextActionIndex));

                stackEffect(stackedEntity);
            }
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
