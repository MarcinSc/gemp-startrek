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
    public void processEffect(Entity sourceEntity, Entity effectEntity, GameEffectComponent gameEffect, Memory memory) {
        String effectType = gameEffect.getType();
        if (effectType.equals("stackUntil")) {
            stackUntil(effectEntity, gameEffect, memory);
        } else if (effectType.equals("stackUntilInTurnOrder")) {
            stackUntilInTurnOrder(effectEntity, gameEffect, memory);
        }
    }

    private void stackUntil(Entity effectEntity, GameEffectComponent gameEffect, Memory memory) {
        String condition = gameEffect.getDataString("condition");
        boolean result = conditionResolverSystem.resolveBoolean(effectEntity, memory, condition);
        if (!result) {
            Entity stackedEntity = world.createEntity();
            JsonValue action = gameEffect.getClonedDataObject("action");
            String actionType = action.getString("type");
            boolean createMemory = action.getBoolean("memory", false);
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            if (createMemory) {
                effectMemoryComponentMapper.create(stackedEntity).setMemoryType("action - " + actionType);
            }
            newGameEffect.setType(actionType);
            newGameEffect.setData(action);

            stackEffect(stackedEntity);
        } else {
            removeTopEffectFromStack();
        }
    }

    private void stackUntilInTurnOrder(Entity effectEntity, GameEffectComponent gameEffect, Memory memory) {
        boolean condition = conditionResolverSystem.resolveBoolean(effectEntity, memory,
                gameEffect.getDataString("condition"));
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
            JsonValue action = gameEffect.getClonedDataObject("action");
            String actionType = action.getString("type");
            boolean createMemory = action.getBoolean("memory", false);

            action.addChild("player", new JsonValue("username(" + player + ")"));
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            if (createMemory) {
                effectMemoryComponentMapper.create(stackedEntity).setMemoryType("action - " + actionType);
            }
            newGameEffect.setType(actionType);
            newGameEffect.setData(action);
            memory.setValue("playerIndex", String.valueOf(nextPlayerIndex));

            stackEffect(stackedEntity);
        } else {
            removeTopEffectFromStack();
        }
    }
}
