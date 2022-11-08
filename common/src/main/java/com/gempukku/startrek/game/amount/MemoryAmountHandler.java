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
        return Integer.parseInt(memory.getValue(parameters.get(0)));
    }
}
