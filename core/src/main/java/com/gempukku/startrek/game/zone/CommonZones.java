package com.gempukku.startrek.game.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.common.OrderComponent;

import java.util.Comparator;

public class CommonZones {
    private ObjectMap<Entity, Entity> objectToRenderedMap = new ObjectMap<>();
    private final Array<Entity> objectsOnStack = new Array<>();
    private final Comparator<Entity> stackOrderComparator = new Comparator<Entity>() {
        @Override
        public int compare(Entity o1, Entity o2) {
            return o1.getComponent(OrderComponent.class).getValue() - o2.getComponent(OrderComponent.class).getValue();
        }
    };

    public void addObjectToStack(Entity entity, Entity renderedEntity) {
        objectToRenderedMap.put(entity, renderedEntity);
        objectsOnStack.add(renderedEntity);
    }

    public Entity removeObjectFromStack(Entity entity) {
        Entity renderedEntity = objectToRenderedMap.remove(entity);
        objectsOnStack.removeValue(renderedEntity, true);
        return renderedEntity;
    }

    public Array<Entity> getObjectsOnStack() {
        objectsOnStack.sort(stackOrderComparator);
        return objectsOnStack;
    }
}
