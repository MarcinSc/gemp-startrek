package com.gempukku.startrek.server.game.effect;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.server.game.ExecuteStackedAction;

public class GameEffectSystem extends BaseSystem {
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
            action.setFinishedProcessing(gameEffectHandler.processEndingEffect(gameEffectEntity, gameEffect));
        }
    }

    @Override
    protected void processSystem() {

    }
}
