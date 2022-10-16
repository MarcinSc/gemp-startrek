package com.gempukku.libgdx.lib.graph.artemis.sprite;

import com.badlogic.gdx.utils.ObjectMap;

public class SpriteDefinition {
    private String spriteSystemName;
    private ObjectMap<String, Object> properties = new ObjectMap<>();

    public String getSpriteSystemName() {
        return spriteSystemName;
    }

    public void setSpriteSystemName(String spriteSystemName) {
        this.spriteSystemName = spriteSystemName;
    }

    public ObjectMap<String, Object> getProperties() {
        return properties;
    }
}
