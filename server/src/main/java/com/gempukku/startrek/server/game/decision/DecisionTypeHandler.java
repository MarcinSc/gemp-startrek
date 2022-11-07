package com.gempukku.startrek.server.game.decision;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public interface DecisionTypeHandler {
    boolean validateDecision(String decisionPlayer, JsonValue decisionData, ObjectMap<String, String> result);

    void processDecision(String decisionPlayer, JsonValue decisionData, ObjectMap<String, String> result);
}
