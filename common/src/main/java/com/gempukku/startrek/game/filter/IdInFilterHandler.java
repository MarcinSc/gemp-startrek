package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;

public class IdInFilterHandler extends CardFilterSystem {
    private IdProviderSystem idProviderSystem;

    public IdInFilterHandler() {
        super("idIn");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        if (parameters.size == 1 && parameters.get(0).equals("")) {
            return new CardFilter() {
                @Override
                public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                    return false;
                }
            };
        }

        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                String id = idProviderSystem.getEntityId(cardEntity);
                for (String parameter : parameters) {
                    if (parameter.equals(id))
                        return true;
                }

                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {

    }
}
