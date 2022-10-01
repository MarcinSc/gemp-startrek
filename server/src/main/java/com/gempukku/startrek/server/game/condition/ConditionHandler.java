package com.gempukku.startrek.server.game.condition;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public interface ConditionHandler {
    boolean resolveCondition(ObjectMap<String, String> memory, String type, Array<String> parameters);
}
