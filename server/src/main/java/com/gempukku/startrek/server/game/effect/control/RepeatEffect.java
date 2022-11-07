package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.server.JsonValueHandler;
import com.gempukku.startrek.server.game.amount.AmountResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class RepeatEffect extends EffectSystem {
    private AmountResolverSystem amountResolverSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public RepeatEffect() {
        super("repeat", "repeatForPlayer");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String type = gameEffect.getType();
        if (type.equals("repeat")) {
            repeat(gameEffectEntity, gameEffect);
        } else if (type.equals("repeatForPlayer")) {
            repeatForPlayer(gameEffectEntity, gameEffect);
        }
    }

    private void repeat(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        ObjectMap<String, String> memory = gameEffect.getMemory();
        int times = amountResolverSystem.resolveAmount(gameEffectEntity, memory, gameEffect.getData().getString("times"));

        int executedTimes = 0;
        String executed = memory.get("times");
        if (executed != null) {
            executedTimes = Integer.parseInt(executed);
        }

        if (executedTimes < times) {
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            JsonValue action = JsonValueHandler.clone(gameEffect.getData().get("action"));
            newGameEffect.setType(action.getString("type"));
            newGameEffect.setData(action);

            executedTimes++;
            memory.put("times", String.valueOf(executedTimes));

            stackEffect(stackedEntity);
        } else {
            removeEffectFromStack(gameEffectEntity);
        }
    }

    private void repeatForPlayer(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        ObjectMap<String, String> memory = gameEffect.getMemory();
        int times = amountResolverSystem.resolveAmount(gameEffectEntity, memory, gameEffect.getData().getString("times"));
        String player = gameEffect.getData().getString("player");

        int executedTimes = 0;
        String executed = memory.get("times");
        if (executed != null) {
            executedTimes = Integer.parseInt(executed);
        }

        if (executedTimes < times) {
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            JsonValue action = JsonValueHandler.clone(gameEffect.getData().get("action"));
            action.addChild("player", new JsonValue(player));
            newGameEffect.setType(action.getString("type"));
            newGameEffect.setData(action);

            executedTimes++;
            memory.put("times", String.valueOf(executedTimes));

            stackEffect(stackedEntity);
        } else {
            removeEffectFromStack(gameEffectEntity);
        }
    }
}
