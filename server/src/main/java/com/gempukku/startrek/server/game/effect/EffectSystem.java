package com.gempukku.startrek.server.game.effect;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.gempukku.startrek.game.Memory;
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

    protected void removeTopEffectFromStack() {
        Entity entity = stackSystem.removeTopStackEntity();
        world.deleteEntity(entity);
    }

    protected void stackEffect(Entity effectEntity) {
        stackSystem.stackEntity(effectEntity);
    }

    @Override
    public boolean processEndingEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        processEffect(sourceEntity, gameEffect, memory);
        return false;
    }

    protected abstract void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory);

    @Override
    protected void processSystem() {

    }
}
