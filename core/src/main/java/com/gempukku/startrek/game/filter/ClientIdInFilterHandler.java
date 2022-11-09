package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;

public class ClientIdInFilterHandler extends CardFilterSystem {
    public ClientIdInFilterHandler() {
        super("idIn");
    }

    @Override
    public CardFilter resolveFilter(String filterType, Array<String> parameters) {
        if (parameters.size == 1 && parameters.get(0).equals("")) {
            return new CardFilter() {
                @Override
                public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                    return true;
                }
            };
        }
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                String id = String.valueOf(cardEntity.getComponent(ServerCardReferenceComponent.class).getEntityId());
                for (String parameter : parameters) {
                    if (parameter.equals(id))
                        return true;
                }

                return false;
            }
        };
    }
}
