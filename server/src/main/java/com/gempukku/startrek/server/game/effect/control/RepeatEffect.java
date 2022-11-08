package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class RepeatEffect extends EffectSystem {
    private AmountResolverSystem amountResolverSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public RepeatEffect() {
        super("repeat", "repeatForPlayer");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, Memory memory) {
        String type = gameEffect.getType();
        if (type.equals("repeat")) {
            repeat(gameEffectEntity, gameEffect, memory);
        } else if (type.equals("repeatForPlayer")) {
            repeatForPlayer(gameEffectEntity, gameEffect, memory);
        }
    }

    private void repeat(Entity gameEffectEntity, GameEffectComponent gameEffect, Memory memory) {
        int times = amountResolverSystem.resolveAmount(gameEffectEntity, memory,
                gameEffect.getDataString("times"));

        int executedTimes = 0;
        String executed = memory.getValue("times");
        if (executed != null) {
            executedTimes = Integer.parseInt(executed);
        }

        if (executedTimes < times) {
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            JsonValue action = gameEffect.getClonedDataObject("action");
            newGameEffect.setType(action.getString("type"));
            newGameEffect.setData(action);

            executedTimes++;
            memory.setValue("times", String.valueOf(executedTimes));

            stackEffect(stackedEntity);
        } else {
            removeEffectFromStack(gameEffectEntity);
        }
    }

    private void repeatForPlayer(Entity gameEffectEntity, GameEffectComponent gameEffect, Memory memory) {
        int times = amountResolverSystem.resolveAmount(gameEffectEntity, memory,
                gameEffect.getDataString("times"));
        String player = gameEffect.getDataString("player");

        int executedTimes = 0;
        String executed = memory.getValue("times");
        if (executed != null) {
            executedTimes = Integer.parseInt(executed);
        }

        if (executedTimes < times) {
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            JsonValue action = gameEffect.getClonedDataObject("action");
            action.addChild("player", new JsonValue(player));
            newGameEffect.setType(action.getString("type"));
            newGameEffect.setData(action);

            executedTimes++;
            memory.setValue("times", String.valueOf(executedTimes));

            stackEffect(stackedEntity);
        } else {
            removeEffectFromStack(gameEffectEntity);
        }
    }
}
