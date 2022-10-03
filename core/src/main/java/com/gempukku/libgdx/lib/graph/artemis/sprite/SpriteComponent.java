package com.gempukku.libgdx.lib.graph.artemis.sprite;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.ObjectMap;

public class SpriteComponent extends PooledComponent {
    private String spriteSystemName;
    private ObjectMap<String, Object> properties = new ObjectMap<>();

    public String getSpriteSystemName() {
        return spriteSystemName;
    }

    public ObjectMap<String, Object> getProperties() {
        return properties;
    }

    @Override
    protected void reset() {
        spriteSystemName = null;
        properties.clear();
    }
}
