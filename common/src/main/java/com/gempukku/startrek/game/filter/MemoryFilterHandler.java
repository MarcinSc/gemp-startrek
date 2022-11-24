package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class MemoryFilterHandler extends CardFilterSystem {
    private IdProviderSystem idProviderSystem;
    public MemoryFilterHandler() {
        super("memory");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                String matchingCardId = idProviderSystem.getEntityId(cardEntity);
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
