package com.gempukku.startrek.server.game.decision;

import com.badlogic.gdx.utils.ObjectMap;

public interface DecisionTypeHandler {
    boolean validateDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result);

    void processDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result);
}
