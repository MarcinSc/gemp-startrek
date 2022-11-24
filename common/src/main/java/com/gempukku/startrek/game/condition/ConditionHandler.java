package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;

public interface ConditionHandler {
    boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters);

    void validate(Array<String> parameters);
}
