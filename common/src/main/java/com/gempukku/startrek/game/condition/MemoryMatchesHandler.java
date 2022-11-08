package com.gempukku.startrek.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;

public class MemoryMatchesHandler extends ConditionSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardFilteringSystem cardFilteringSystem;

    public MemoryMatchesHandler() {
        super("memoryMatches");
    }

    @Override
    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
        String memoryName = parameters.get(0);
        CardFilter cardFilter = cardFilterResolverSystem.createAndFilter(parameters, 1);
        String cardIds = memory.getValue(memoryName);
        String[] cardIdsSplit = cardIds.split(",");
        for (String cardIdStr : cardIdsSplit) {
            int cardId = Integer.parseInt(cardIdStr);
            Entity entity = world.getEntity(cardId);
            if (cardFilter.accepts(sourceEntity, memory, entity))
                return true;
        }

        return false;
    }
}
