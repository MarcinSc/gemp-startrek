package com.gempukku.startrek.server.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterSystem;

public class MemoryFilterHandler extends CardFilterSystem {
    private ServerEntityIdSystem serverEntityIdSystem;
    public MemoryFilterHandler() {
        super("memory");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                String matchingCardId = serverEntityIdSystem.getEntityId(cardEntity);
                String cardIds = memory.getValue(parameters.get(0));
                if (cardIds == null)
                    return false;
                String[] cardIdSplit = cardIds.split(",");
                for (String cardId : cardIdSplit) {
                    if (cardId.equals(matchingCardId))
                        return true;
                }

                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
    }
}
