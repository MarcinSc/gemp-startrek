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
        CardFilter filter = cardFilteringSystem.createAndFilter(parameters);

        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                Array<Entity> onBoard = cardFilteringSystem.findAllInPlay(sourceEntity, memory, "attachedTo(idIn(" + idProviderSystem.getEntityId(cardEntity) + "))");
                for (Entity entity : onBoard) {
                    if (filter.accepts(sourceEntity, memory, entity))
                        return true;
                }

                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        for (String parameter : parameters) {
            cardFilteringSystem.validateFilter(parameter);
        }
    }
}
