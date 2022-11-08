package com.gempukku.startrek.server.game.effect;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.server.game.stack.ExecuteStackedAction;
import com.gempukku.startrek.server.game.stack.StackSystem;

public class GameEffectSystem extends BaseSystem {
    private StackSystem stackSystem;
    private ObjectMap<String, GameEffectHandler> gameEffectHandlers = new ObjectMap<>();

    public void registerGameEffectHandler(String effectType, GameEffectHandler gameEffectHandler) {
        gameEffectHandlers.put(effectType, gameEffectHandler);
    }

    @EventListener
    public void playoutGameEffect(ExecuteStackedAction action, Entity gameEffectEntity) {
        GameEffectComponent gameEffect = gameEffectEntity.getComponent(GameEffectComponent.class);
        if (gameEffect != null) {
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
            action.setFinishedProcessing(gameEffectHandler.processEndingEffect(gameEffectEntity, gameEffect, memory));
        }
    }

    @Override
    protected void processSystem() {

    }
}
