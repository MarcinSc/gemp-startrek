package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;

public interface AmountHandler {
    int resolveAmount(String type, Entity sourceEntity, Memory memory, Array<String> parameters);

    void validate(Array<String> parameters);
}
