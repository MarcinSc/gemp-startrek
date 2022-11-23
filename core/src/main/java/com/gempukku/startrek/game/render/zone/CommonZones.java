package com.gempukku.startrek.game.render.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.common.OrderComponent;
import com.gempukku.startrek.game.zone.CardZone;

import java.util.Comparator;

public class CommonZones {
    private final ObjectMap<Entity, Entity> serverToRenderedMap;
    private final Array<Entity> objectsOnStack = new Array<>();
    private final Comparator<Entity> stackOrderComparator = new Comparator<Entity>() {
        @Override
        public int compare(Entity o1, Entity o2) {
            return o1.getComponent(OrderComponent.class).getValue() - o2.getComponent(OrderComponent.class).getValue();
        }
    };

    private boolean stackDirty;

    public CommonZones(ObjectMap<Entity, Entity> serverToRenderedMap,
                       ObjectMap<Entity, RenderedCardGroup> attachedCards) {
        this.serverToRenderedMap = serverToRenderedMap;
    }


    public void addObjectToStack(Entity entity, Entity renderedEntity) {
        serverToRenderedMap.put(entity, renderedEntity);
        objectsOnStack.add(renderedEntity);
        stackDirty = true;
    }

    public Entity removeObjectFromStack(Entity entity) {
        if (objectsOnStack.contains(serverToRenderedMap.get(entity), true)) {
            Entity renderedEntity = serverToRenderedMap.remove(entity);
            objectsOnStack.removeValue(renderedEntity, true);
            stackDirty = true;
            return renderedEntity;
        }
        return null;
    }

    public Array<Entity> getObjectsOnStack() {
        objectsOnStack.sort(stackOrderComparator);
        return objectsOnStack;
    }

    public Entity findRenderedCard(Entity card) {
        return serverToRenderedMap.get(card);
    }

    public boolean isStackDirty() {
        return stackDirty;
    }

    public void cleanup() {
        stackDirty = false;
    }

    public Entity removeFaceUpCard(Entity cardEntity, CardZone zone) {
        if (zone == CardZone.Stack)
            return removeObjectFromStack(cardEntity);
        return null;
    }
}
