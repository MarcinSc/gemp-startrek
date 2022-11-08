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
    public void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String type = gameEffect.getType();
        if (type.equals("repeat")) {
            repeat(sourceEntity, gameEffect, memory);
        } else if (type.equals("repeatForPlayer")) {
            repeatForPlayer(sourceEntity, gameEffect, memory);
        }
    }

    private void repeat(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        int times = amountResolverSystem.resolveAmount(sourceEntity, memory,
                gameEffect.getDataString("times"));

        int executedTimes = 0;
        String executed = memory.getValue("times");
        if (executed != null) {
            executedTimes = Integer.parseInt(executed);
        }

        if (executedTimes < times) {
            JsonValue action = gameEffect.getClonedDataObject("action");
            Entity actionToStack = createActionFromJson(action, sourceEntity);
            executedTimes++;
            memory.setValue("times", String.valueOf(executedTimes));

            stackEffect(actionToStack);
        } else {
            removeTopEffectFromStack();
        }
    }

    private void repeatForPlayer(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        int times = amountResolverSystem.resolveAmount(sourceEntity, memory,
                gameEffect.getDataString("times"));
        String player = gameEffect.getDataString("player");

        int executedTimes = 0;
        String executed = memory.getValue("times");
        if (executed != null) {
            executedTimes = Integer.parseInt(executed);
        }

        if (executedTimes < times) {
            JsonValue action = gameEffect.getClonedDataObject("action");
            action.addChild("player", new JsonValue(player));
            Entity actionToStack = createActionFromJson(action, sourceEntity);

            executedTimes++;
            memory.setValue("times", String.valueOf(executedTimes));

            stackEffect(actionToStack);
        } else {
            removeTopEffectFromStack();
        }
    }
}
