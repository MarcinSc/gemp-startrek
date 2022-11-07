package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;
import com.gempukku.startrek.server.game.condition.ConditionResolverSystem;
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
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        String effectType = gameEffect.getType();
        if (effectType.equals("stackUntil")) {
            stackUntil(gameEffectEntity, gameEffect, memory);
        } else if (effectType.equals("stackUntilInTurnOrder")) {
            stackUntilInTurnOrder(gameEffectEntity, gameEffect, memory);
        }
    }

    private void stackUntil(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        String condition = gameEffect.getDataString("condition");
        boolean result = conditionResolverSystem.resolveBoolean(gameEffectEntity, memory, condition);
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
            removeEffectFromStack(gameEffectEntity);
        }
    }

    private void stackUntilInTurnOrder(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        boolean condition = conditionResolverSystem.resolveBoolean(gameEffectEntity, memory,
                gameEffect.getDataString("condition"));
        if (!condition) {
            TurnSequenceComponent turnSequence = LazyEntityUtil.findEntityWithComponent(world, TurnSequenceComponent.class).
                    getComponent(TurnSequenceComponent.class);
            Array<String> players = turnSequence.getPlayers();

            String playerIndex = memory.get("playerIndex");
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
            memory.put("playerIndex", String.valueOf(nextPlayerIndex));

            stackEffect(stackedEntity);
        } else {
            removeEffectFromStack(gameEffectEntity);
        }
    }
}
