package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;

public class HasCardConditionHandler extends ConditionSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;

    public HasCardConditionHandler() {
        super("hasCard");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        CardFilter cardFilter = cardFilterResolverSystem.createAndFilter(parameters);
        Entity card = cardFilteringSystem.findFirstCardInPlay(sourceEntity, memory, cardFilter);
        return card != null;
    }
}
