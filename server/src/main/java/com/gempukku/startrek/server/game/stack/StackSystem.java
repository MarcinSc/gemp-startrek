package com.gempukku.startrek.server.game.stack;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;

public class StackSystem extends BaseEntitySystem {
    private EventSystem eventSystem;

    private Entity executionStackEntity;
    private ExecutionStackComponent executionStack;

    public StackSystem() {
        super(Aspect.all(ExecutionStackComponent.class));
    }

    @Override
    protected void inserted(int entityId) {
        executionStackEntity = world.getEntity(entityId);
        executionStack = executionStackEntity.getComponent(ExecutionStackComponent.class);
    }

    public Entity getTopMostStackEntity() {
        Array<Integer> entityIds = executionStack.getEntityIds();
        return world.getEntity(entityIds.get(entityIds.size - 1));
    }

    public void processStack() {
        ExecuteStackedAction executeStackedAction = new ExecuteStackedAction();
        do {
            Entity topMostStackEntity = getTopMostStackEntity();
            eventSystem.fireEvent(executeStackedAction, topMostStackEntity);
            world.process();
        } while (!executeStackedAction.isFinishedProcessing());
    }

    public void stackEntity(Entity effectEntity) {
        Array<Integer> entityIds = executionStack.getEntityIds();
        entityIds.add(effectEntity.getId());
    }

    public Entity removeTopStackEntity() {
        Array<Integer> entityIds = executionStack.getEntityIds();
        Integer removedId = entityIds.removeIndex(entityIds.size - 1);
        return world.getEntity(removedId);
    }

    public Entity peekTopStackEntity() {
        Array<Integer> entityIds = executionStack.getEntityIds();
        Integer entityId = entityIds.get(entityIds.size - 1);
        return world.getEntity(entityId);
    }

    @Override
    protected void processSystem() {

    }
}
