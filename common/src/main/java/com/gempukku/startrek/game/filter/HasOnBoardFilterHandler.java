package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class HasOnBoardFilterHandler extends CardFilterSystem {
    private CardFilteringSystem cardFilteringSystem;
    private IdProviderSystem idProviderSystem;

    public HasOnBoardFilterHandler() {
        super("hasOnBoard");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        CardFilter filter = cardFilteringSystem.createAndFilter(parameters, 1);

        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardFilter attachedFilter = cardFilteringSystem.resolveCardFilter("attachedTo(idIn(" + idProviderSystem.getEntityId(cardEntity) + "))");
                return cardFilteringSystem.hasCard(sourceEntity, memory, parameters.get(0), attachedFilter, filter);
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        cardFilteringSystem.validateSource(parameters.get(0));
        cardFilteringSystem.validateFilter(parameters, 1);
    }
}
