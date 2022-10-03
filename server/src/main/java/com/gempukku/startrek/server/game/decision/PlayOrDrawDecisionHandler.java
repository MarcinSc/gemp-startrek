package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayOrDrawDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;

    @Override
    protected void initialize() {
        decisionSystem.registerDecisionTypeHandler("playOrDrawDecision", this);
    }

    @Override
    public boolean validateDecision(String decisionType, JsonValue decisionData, ObjectMap<String, String> result) {
        return false;
    }

    @Override
    public void processDecision(String decisionType, JsonValue decisionData, ObjectMap<String, String> result) {

    }

    @Override
    protected void processSystem() {

    }
}
