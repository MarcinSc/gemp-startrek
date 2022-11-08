package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;

public interface ConditionHandler {
    boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters);
}
