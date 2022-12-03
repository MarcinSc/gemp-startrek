package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;

public class SelectArbitraryDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;

    @Override
    protected void initialize() {
        decisionSystem.registerDecisionTypeHandler("selectArbitrary", this);
    }

    @Override
    public boolean validateDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        Array<String> matchingCards = StringUtils.splitToArray(decisionData.get("matchingCards"));
        String[] selectedCards = StringUtils.split(result.get("selectedCards"));

        int min = Integer.parseInt(decisionData.get("min"));
        int max = Integer.parseInt(decisionData.get("max"));

        if (selectedCards.length < min || selectedCards.length > max)
            return false;

        for (String selectedCard : selectedCards) {
            if (!matchingCards.removeValue(selectedCard, false))
                return false;
        }

        return true;
    }

    @Override
    public void processDecision(String decisionPlayer, Memory memory, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        memory.setValue(decisionData.get("resultMemory"), result.get("selectedCards"));
    }

    @Override
    protected void processSystem() {

    }
}
