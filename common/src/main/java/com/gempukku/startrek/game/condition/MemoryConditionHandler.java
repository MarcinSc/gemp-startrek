package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class MemoryConditionHandler extends ConditionSystem {
    public MemoryConditionHandler() {
        super("memoryHas");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        String key = parameters.get(0);
        String value = parameters.get(1);
        String stored = memory.getValue(key);
        return stored != null && stored.equals(value);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 2);
    }
}
