package com.gempukku.startrek.server.game.effect;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.server.game.stack.StackSystem;

public abstract class EffectSystem extends BaseSystem implements GameEffectHandler {
    private GameEffectSystem gameEffectSystem;
    private StackSystem stackSystem;

    private final String[] effectTypes;

    public EffectSystem(String... effectTypes) {
        this.effectTypes = effectTypes;
    }

    @Override
    protected void initialize() {
        for (String effectType : effectTypes) {
            gameEffectSystem.registerGameEffectHandler(effectType, this);
        }
    }

    protected void removeEffectFromStack(Entity effectEntity) {
        Entity topStackEntity = stackSystem.peekTopStackEntity();
        if (topStackEntity != effectEntity)
            throw new RuntimeException("The entity to remove is not top most on stack");

        Entity entity = stackSystem.removeTopStackEntity();
        world.deleteEntity(entity);
    }

    protected void stackEffect(Entity effectEntity) {
        stackSystem.stackEntity(effectEntity);
    }

    @Override
    public boolean processEndingEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory) {
        processEffect(gameEffectEntity, gameEffect, memory);
        return false;
    }

    protected abstract void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, ObjectMap<String, String> memory);

    @Override
    protected void processSystem() {

    }
}
