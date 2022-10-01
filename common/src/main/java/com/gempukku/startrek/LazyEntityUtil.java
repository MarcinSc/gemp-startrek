package com.gempukku.startrek;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Predicate;

import java.util.function.Consumer;

public class LazyEntityUtil {
    public static Entity findEntityWithComponent(World world, Class<? extends Component> component) {
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(component)).getEntities();
        for (int i = 0, s = entities.size(); s > i; i++) {
            int entityId = entities.get(i);
            return world.getEntity(entityId);
        }
        return null;
    }

    public static Entity findEntityWithComponent(World world, Class<? extends Component> component, Predicate<Entity> filter) {
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(component)).getEntities();
        for (int i = 0, s = entities.size(); s > i; i++) {
            int entityId = entities.get(i);
            Entity entity = world.getEntity(entityId);
            if (filter.evaluate(entity))
                return entity;
        }
        return null;
    }

    public static void forEachEntityWithComponent(World world, Class<? extends Component> component, Consumer<Entity> consumer) {
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(component)).getEntities();
        for (int i = 0, s = entities.size(); s > i; i++) {
            int entityId = entities.get(i);
            consumer.accept(world.getEntity(entityId));
        }
    }
}
