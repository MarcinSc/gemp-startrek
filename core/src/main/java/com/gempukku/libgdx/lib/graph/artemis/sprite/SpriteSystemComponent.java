package com.gempukku.libgdx.lib.graph.artemis.sprite;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.ObjectMap;

public class SpriteSystemComponent extends PooledComponent {
    private String name;
    private String renderTag;
    private int spritesPerPage = 16383;
    private final ObjectMap<String, Object> properties = new ObjectMap<>();

    public String getName() {
        return name;
    }

    public String getRenderTag() {
        return renderTag;
    }

    public int getSpritesPerPage() {
        return spritesPerPage;
    }

    public ObjectMap<String, Object> getProperties() {
        return properties;
    }

    @Override
    protected void reset() {
        name = null;
        renderTag = null;
        spritesPerPage = 16383;
        properties.clear();
    }
}
