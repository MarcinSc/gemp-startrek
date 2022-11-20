package com.gempukku.startrek.game.decision;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.ObjectMap;

public class ClientExecuteOrdersDecisionHandler extends BaseSystem implements DecisionHandler {
    private ClientDecisionSystem clientDecisionSystem;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("executeOrdersDecision", this);
    }

    @Override
    public void processNewDecision(ObjectMap<String, String> decisionData) {
        // TODO
    }

    @Override
    protected void processSystem() {

    }
}
