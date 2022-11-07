package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public interface ConditionHandler {
    boolean resolveCondition(String type, Entity sourceEntity, ObjectMap<String, String> memory, Array<String> parameters);
}
