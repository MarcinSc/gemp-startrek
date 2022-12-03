package com.gempukku.startrek.server.game.decision;

import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.game.Memory;

public interface DecisionTypeHandler {
    boolean validateDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result);

    void processDecision(String decisionPlayer, Memory memory, ObjectMap<String, String> decisionData, ObjectMap<String, String> result);
}
