package com.gempukku.startrek.game.decision;

import com.badlogic.gdx.utils.JsonValue;

public interface DecisionHandler {
    void processNewDecision(JsonValue decisionData);
}
