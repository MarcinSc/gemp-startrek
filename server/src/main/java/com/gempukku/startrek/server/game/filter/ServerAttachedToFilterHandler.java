package com.gempukku.startrek.server.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.filter.CardFilterSystem;
import com.gempukku.startrek.game.zone.CardInPlayComponent;

public class ServerAttachedToFilterHandler extends CardFilterSystem {
    private CardFilterResolverSystem cardFilterResolverSystem;
    private ServerEntityIdSystem serverEntityIdSystem;

    public ServerAttachedToFilterHandler() {
        super("attachedTo");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);
                if (cardInPlay != null) {
                    String attachedToId = cardInPlay.getAttachedToId();
                    if (attachedToId != null) {
                        Entity attachedToEntity = serverEntityIdSystem.findfromId(attachedToId);
                        CardFilter filter = cardFilterResolverSystem.createAndFilter(parameters);
                        return filter.accepts(sourceEntity, memory, attachedToEntity);
                    }
                }
                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.atLeast(parameters, 1);
        for (String parameter : parameters) {
            cardFilterResolverSystem.validateFilter(parameter);
        }
    }
}
