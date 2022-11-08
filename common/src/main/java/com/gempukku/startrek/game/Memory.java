package com.gempukku.startrek.game;

import com.badlogic.gdx.utils.ObjectMap;

public class Memory {
    private ObjectMap<String, String> memory;

    public Memory(ObjectMap<String, String> memory) {
        this.memory = memory;
    }

    public String getValue(String name) {
        return memory.get(name);
    }

    public String getValue(String name, String defaultValue) {
        String result = memory.get(name);
        if (result == null)
            return defaultValue;
        return result;
    }

    public void setValue(String name, String value) {
        memory.put(name, value);
    }
}
