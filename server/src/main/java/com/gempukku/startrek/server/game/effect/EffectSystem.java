package com.gempukku.startrek.server.game.effect;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.server.game.ExecutionStackComponent;

public abstract class EffectSystem extends BaseSystem implements GameEffectHandler {
    private GameEffectSystem gameEffectSystem;

    private final String[] effectTypes;
    private ExecutionStackComponent executionStackComponent;

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
        Array<Integer> entityIds = getExecutionStack().getEntityIds();
        Integer topMostEntityId = entityIds.get(entityIds.size - 1);
        if (effectEntity.getId() != topMostEntityId)
            throw new RuntimeException("The entity to remove is not top most on stack");

        entityIds.removeIndex(entityIds.size - 1);
        world.deleteEntity(effectEntity);
    }

    protected void stackEffect(Entity effectEntity) {
        Array<Integer> entityIds = getExecutionStack().getEntityIds();
        entityIds.add(effectEntity.getId());
    }

    private ExecutionStackComponent getExecutionStack() {
        if (executionStackComponent == null) {
            executionStackComponent = LazyEntityUtil.findEntityWithComponent(world, ExecutionStackComponent.class).getComponent(ExecutionStackComponent.class);
        }
        return executionStackComponent;
    }

    @Override
    public boolean processEndingEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        processEffect(gameEffectEntity, gameEffect);
        return false;
    }

    protected abstract void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect);

    @Override
    protected void processSystem() {

    }
}
