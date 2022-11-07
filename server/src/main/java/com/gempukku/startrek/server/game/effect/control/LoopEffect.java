package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;
import com.gempukku.startrek.server.JsonValueHandler;
import com.gempukku.startrek.server.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class LoopEffect extends EffectSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public LoopEffect() {
        super("stackUntil", "stackUntilInTurnOrder");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String effectType = gameEffect.getType();
        if (effectType.equals("stackUntil")) {
            stackUntil(gameEffectEntity, gameEffect);
        } else if (effectType.equals("stackUntilInTurnOrder")) {
            stackUntilInTurnOrder(gameEffectEntity, gameEffect);
        }
    }

    private void stackUntil(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        boolean result = conditionResolverSystem.resolveBoolean(gameEffectEntity, gameEffect.getMemory(),
                gameEffect.getDataString("condition"));
        if (!result) {
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            JsonValue action = JsonValueHandler.clone(gameEffect.getData().get("action"));
            newGameEffect.setType(action.getString("type"));
            newGameEffect.setData(action);

            stackEffect(stackedEntity);
        } else {
            removeEffectFromStack(gameEffectEntity);
        }
    }

    private void stackUntilInTurnOrder(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        boolean condition = conditionResolverSystem.resolveBoolean(gameEffectEntity, gameEffect.getMemory(),
                gameEffect.getDataString("condition"));
        if (!condition) {
            TurnSequenceComponent turnSequence = LazyEntityUtil.findEntityWithComponent(world, TurnSequenceComponent.class).
                    getComponent(TurnSequenceComponent.class);
            Array<String> players = turnSequence.getPlayers();

            String playerIndex = gameEffect.getMemory().get("playerIndex");
            int nextPlayerIndex = 0;
            if (playerIndex != null) {
                nextPlayerIndex = Integer.parseInt(playerIndex) + 1;
            }

            if (nextPlayerIndex == players.size)
                nextPlayerIndex = 0;

            String player = players.get(nextPlayerIndex);
            JsonValue action = JsonValueHandler.clone(gameEffect.getData().get("action"));

            action.addChild("player", new JsonValue("username(" + player + ")"));
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            newGameEffect.setType(action.getString("type"));
            newGameEffect.setData(action);
            gameEffect.getMemory().put("playerIndex", String.valueOf(nextPlayerIndex));

            stackEffect(stackedEntity);
        } else {
            removeEffectFromStack(gameEffectEntity);
        }
    }
}
