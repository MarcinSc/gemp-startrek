package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class MissionPointsAmountHandler extends AmountSystem {
    private CardLookupSystem cardLookupSystem;

    public MissionPointsAmountHandler() {
        super("missionPoints");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(sourceEntity);
        return cardDefinition.getPoints();
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
