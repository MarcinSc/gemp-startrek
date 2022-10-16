package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

public class TextStyle implements Pool.Poolable {
    private ObjectMap<String, Object> attributes = new ObjectMap<>();

    public ObjectMap<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public void reset() {
        attributes.clear();
    }
}
