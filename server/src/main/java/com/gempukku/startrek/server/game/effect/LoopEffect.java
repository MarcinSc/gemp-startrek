package com.gempukku.startrek.server.game.effect;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.server.game.condition.ConditionResolverSystem;

public class LoopEffect extends EffectSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public LoopEffect() {
        super("stackUntil");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        JsonValue condition = gameEffect.getData().get("condition");
        boolean result = conditionResolverSystem.resolveBoolean(gameEffect.getMemory(), condition);
        if (!result) {
            Entity stackedEntity = world.createEntity();
            GameEffectComponent newGameEffect = gameEffectComponentMapper.create(stackedEntity);
            JsonValue action = gameEffect.getData().get("action");
            newGameEffect.setType(action.getString("type"));
            newGameEffect.setData(action);

            stackEffect(stackedEntity);
        } else {
            removeEffectFromStack(gameEffectEntity);
        }
    }
}
