package com.gempukku.startrek.server.game.condition;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.condition.ConditionSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class MemoryMatchesHandler extends ConditionSystem {
    private CardFilteringSystem cardFilteringSystem;
    private ServerEntityIdSystem serverEntityIdSystem;

    public MemoryMatchesHandler() {
        super("memoryMatches");
    }

    @Override
    public boolean resolveCondition(Entity sourceEntity, Memory memory, Array<String> parameters) {
        String memoryName = parameters.get(0);
        CardFilter cardFilter = cardFilteringSystem.createAndFilter(parameters, 1);
        String cardIds = memory.getValue(memoryName);
        String[] cardIdsSplit = StringUtils.split(cardIds);
        for (String cardId : cardIdsSplit) {
            Entity entity = serverEntityIdSystem.findfromId(cardId);
            if (cardFilter.accepts(sourceEntity, memory, entity))
                return true;
        }

        return false;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
    }
}
