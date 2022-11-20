package com.gempukku.startrek.server.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterSystem;

public class ServerIdInFilterHandler extends CardFilterSystem {
    private ServerEntityIdSystem serverEntityIdSystem;

    public ServerIdInFilterHandler() {
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
                String id = serverEntityIdSystem.getEntityId(cardEntity);
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
