package com.gempukku.startrek;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class ComponentUtil {
    private static Json json = new Json();

    static {
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public static void debugComponents(Entity entity) {
        Bag<Component> componentBag = new Bag<>();
        entity.getComponents(componentBag);
        for (int i = 0; i < componentBag.size(); i++) {
            Component component = componentBag.get(i);
            System.out.println(component.getClass().getSimpleName() + ":");
            System.out.println(json.toJson(component));
        }
    }
}
