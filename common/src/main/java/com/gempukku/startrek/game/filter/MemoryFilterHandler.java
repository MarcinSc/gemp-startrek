package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;

public class MemoryFilterHandler extends CardFilterSystem {
    public MemoryFilterHandler() {
        super("memory");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                String matchingCardId = String.valueOf(cardEntity.getId());
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
}
