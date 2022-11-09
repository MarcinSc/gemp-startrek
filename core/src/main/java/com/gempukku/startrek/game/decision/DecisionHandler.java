package com.gempukku.startrek.game.decision;

import com.badlogic.gdx.utils.ObjectMap;

public interface DecisionHandler {
    void processNewDecision(ObjectMap<String, String> decisionData);
}
