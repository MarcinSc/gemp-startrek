package com.gempukku.startrek;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;

public class ComponentUtil {
    public static void debugComponents(Entity entity) {
        Bag<Component> componentBag = new Bag<>();
        entity.getComponents(componentBag);
        for (int i = 0; i < componentBag.size(); i++) {
            System.out.println(componentBag.get(i));
        }
    }
}
