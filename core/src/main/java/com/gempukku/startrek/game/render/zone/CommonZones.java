package com.gempukku.startrek.game.render.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.common.OrderComponent;
import com.gempukku.startrek.game.zone.CardZone;

import java.util.Comparator;

public class CommonZones {
    private final CommonZonesStatus commonZonesStatus = new CommonZonesStatus();

    private final ObjectMap<Entity, Entity> objectToRenderedMap = new ObjectMap<>();
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

        commonZonesStatus.setStackDirty();
    }

    public Entity removeObjectFromStack(Entity entity) {
        Entity renderedEntity = objectToRenderedMap.remove(entity);
        objectsOnStack.removeValue(renderedEntity, true);
        commonZonesStatus.setStackDirty();
        return renderedEntity;
    }

    public Array<Entity> getObjectsOnStack() {
        objectsOnStack.sort(stackOrderComparator);
        return objectsOnStack;
    }

    public Entity findRenderedCard(Entity card) {
        return objectToRenderedMap.get(card);
    }

    public boolean isStackDirty() {
        return commonZonesStatus.isStackDirty();
    }

    public void cleanup() {
        commonZonesStatus.cleanup();
    }

    public Entity removeObject(Entity entity, CardZone oldZone) {
        if (oldZone == CardZone.Stack)
            return removeObjectFromStack(entity);
        return null;
    }
}
