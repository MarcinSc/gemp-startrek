package com.gempukku.startrek.server.game.condition;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MemoryConditionHandler extends ConditionSystem {
    public MemoryConditionHandler() {
        super("memoryHas");
    }

    @Override
    public boolean resolveCondition(ObjectMap<String, String> memory, String type, Array<String> parameters) {
        String key = parameters.get(0);
        String value = parameters.get(1);
        String stored = memory.get(key);
        return stored != null && stored.equals(value);
    }
}
