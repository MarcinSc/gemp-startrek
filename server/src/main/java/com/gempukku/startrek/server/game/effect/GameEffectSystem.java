package com.gempukku.startrek.server.game.effect;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.server.game.stack.ExecuteStackedAction;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class GameEffectSystem extends BaseSystem {
    private ExecutionStackSystem stackSystem;
    private IdProviderSystem idProviderSystem;

    private ObjectMap<String, GameEffectHandler> gameEffectHandlers = new ObjectMap<>();

    public void registerGameEffectHandler(String effectType, GameEffectHandler gameEffectHandler) {
        gameEffectHandlers.put(effectType, gameEffectHandler);
    }

    public void validate(JsonValue effect) {
        String type = effect.getString("type");
        GameEffectHandler gameEffectHandler = gameEffectHandlers.get(type);
        if (gameEffectHandler == null)
            throw new RuntimeException("Unable to find game effect handler for type: " + type);
        gameEffectHandler.validate(effect);
    }

    @EventListener
    public void playoutGameEffect(ExecuteStackedAction action, Entity effectEntity) {
        GameEffectComponent gameEffect = effectEntity.getComponent(GameEffectComponent.class);
        if (gameEffect != null) {
            Entity sourceEntity = null;
            String sourceEntityId = gameEffect.getSourceEntityId();
            if (sourceEntityId != null)
                sourceEntity = idProviderSystem.getEntityById(sourceEntityId);
            String type = gameEffect.getType();
            GameEffectHandler gameEffectHandler = gameEffectHandlers.get(type);
            if (gameEffectHandler == null) {
                throw new RuntimeException("Unable to find game effect handler for type: " + type);
            }
            Entity effectMemoryEntity = stackSystem.getTopMostStackEntityWithComponent(EffectMemoryComponent.class);
            if (effectMemoryEntity == null)
                throw new GdxRuntimeException("Unable to find an effect with memory on the stack");

            EffectMemoryComponent effectMemory = effectMemoryEntity.getComponent(EffectMemoryComponent.class);
            Memory memory = new Memory(effectMemory.getMemory());
            action.setFinishedProcessing(gameEffectHandler.processEndingEffect(sourceEntity, memory, effectEntity, gameEffect));
        }
    }

    @Override
    protected void processSystem() {

    }
}
