package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.server.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;

public class LoopEffect extends EffectSystem {
    private ConditionResolverSystem conditionResolverSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public LoopEffect() {
        super("stackUntil");
    }

    @Override
    public void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        boolean result = conditionResolverSystem.resolveBoolean(gameEffectEntity, gameEffect.getMemory(), gameEffect.getData().getString("condition"));
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
