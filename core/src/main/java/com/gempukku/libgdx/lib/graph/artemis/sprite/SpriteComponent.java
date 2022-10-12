package com.gempukku.libgdx.lib.graph.artemis.sprite;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class SpriteComponent extends PooledComponent {
    private ObjectMap<String, Object> properties = new ObjectMap<>();
    private Array<SpriteDefinition> sprites = new Array<>();

    public ObjectMap<String, Object> getProperties() {
        return properties;
    }

    public Array<SpriteDefinition> getSprites() {
        return sprites;
    }

    @Override
    protected void reset() {
        sprites.clear();
        properties.clear();
    }
}
