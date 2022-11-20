package com.gempukku.startrek.server.game.stack;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.zone.ObjectOnStackComponent;

public class ObjectStackSystem extends BaseEntitySystem {
    private Entity executionStackEntity;
    private ObjectStackComponent executionStack;

    public ObjectStackSystem() {
        super(Aspect.all(ObjectStackComponent.class));
    }

    @Override
    protected void inserted(int entityId) {
        executionStackEntity = world.getEntity(entityId);
        executionStack = executionStackEntity.getComponent(ObjectStackComponent.class);
    }

    public void stackEntity(Entity objectEntity) {
        Array<Integer> entityIds = executionStack.getEntityIds();
        ObjectOnStackComponent objectOnStack = objectEntity.getComponent(ObjectOnStackComponent.class);
        objectOnStack.setStackIndex(entityIds.size);
        entityIds.add(objectEntity.getId());
    }

    public void removeFromStack(Entity objectEntity) {
        Array<Integer> entityIds = executionStack.getEntityIds();
        entityIds.removeValue(objectEntity.getId(), false);
    }

    public Entity removeTopMostFromStack() {
        Array<Integer> entityIds = executionStack.getEntityIds();
        return world.getEntity(entityIds.removeIndex(entityIds.size - 1));
    }

    @Override
    protected void processSystem() {

    }
}
