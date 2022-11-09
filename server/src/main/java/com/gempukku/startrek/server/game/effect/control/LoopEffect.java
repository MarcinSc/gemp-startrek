package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class LoopEffect extends EffectSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;
    private ComponentMapper<EffectMemoryComponent> effectMemoryComponentMapper;

    public LoopEffect() {
        super("stackUntil", "stackUntilInTurnOrder");
    }

    @Override
    public void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String effectType = gameEffect.getType();
        if (effectType.equals("stackUntil")) {
            stackUntil(sourceEntity, gameEffect, memory);
        } else if (effectType.equals("stackUntilInTurnOrder")) {
            stackUntilInTurnOrder(sourceEntity, gameEffect, memory);
        }
    }

    private void stackUntil(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String condition = gameEffect.getDataString("condition");
        boolean result = conditionResolverSystem.resolveBoolean(sourceEntity, memory, condition);
        if (!result) {
            JsonValue action = gameEffect.getClonedDataObject("action");
            Entity actionToStack = createActionFromJson(action, sourceEntity);
            stackEffect(actionToStack);
        } else {
            removeTopEffectFromStack();
        }
    }

    private void stackUntilInTurnOrder(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        boolean condition = conditionResolverSystem.resolveBoolean(sourceEntity, memory,
                gameEffect.getDataString("condition"));
        String playerMemoryName = gameEffect.getDataString("playerMemory");
        if (!condition) {
            TurnSequenceComponent turnSequence = LazyEntityUtil.findEntityWithComponent(world, TurnSequenceComponent.class).
                    getComponent(TurnSequenceComponent.class);
            Array<String> players = turnSequence.getPlayers();

            String playerIndex = memory.getValue("playerIndex");
            int nextPlayerIndex = 0;
            if (playerIndex != null) {
                nextPlayerIndex = Integer.parseInt(playerIndex) + 1;
            }

            if (nextPlayerIndex == players.size)
                nextPlayerIndex = 0;

            String player = players.get(nextPlayerIndex);
            memory.setValue(playerMemoryName, player);
            JsonValue action = gameEffect.getClonedDataObject("action");
            memory.setValue("playerIndex", String.valueOf(nextPlayerIndex));

            Entity actionToStack = createActionFromJson(action, sourceEntity);

            stackEffect(actionToStack);
        } else {
            memory.removeValue(playerMemoryName);
            memory.removeValue("playerIndex");
            removeTopEffectFromStack();
        }
    }
}
