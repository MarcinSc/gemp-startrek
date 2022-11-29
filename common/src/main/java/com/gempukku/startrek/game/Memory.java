package com.gempukku.startrek.game;

import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.common.StringUtils;

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

    public void appendValue(String name, String appended) {
        String source = getValue(name);
        if (source == null)
            setValue(name, appended);
        else
            setValue(name, source + StringUtils.getDefaultDelimiter() + appended);
    }

    public void removeValue(String name) {
        memory.remove(name);
    }
}
