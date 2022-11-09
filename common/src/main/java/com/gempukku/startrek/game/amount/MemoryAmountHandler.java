package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;

public class MemoryAmountHandler extends AmountSystem {
    public MemoryAmountHandler() {
        super("memory");
    }

    @Override
    public int resolveAmount(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        String value = memory.getValue(parameters.get(0));
        if (value == null)
            return 0;
        return Integer.parseInt(value);
    }
}
