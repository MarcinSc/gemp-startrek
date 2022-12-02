package com.gempukku.startrek.game.amount;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class CardMatchingCount extends AmountSystem {
    private CardFilteringSystem cardFilteringSystem;

    public CardMatchingCount() {
        super("cardMatchingCount");
    }

    @Override
    public int resolveAmount(Entity sourceEntity, Memory memory, Array<String> parameters) {
        CardFilter filter = cardFilteringSystem.createAndFilter(parameters, 1);
        return cardFilteringSystem.countMatchingCards(sourceEntity, memory, parameters.get(0), filter);
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 2);
        cardFilteringSystem.validateSource(parameters.get(0));
        cardFilteringSystem.validateFilter(parameters, 1);
    }
}
