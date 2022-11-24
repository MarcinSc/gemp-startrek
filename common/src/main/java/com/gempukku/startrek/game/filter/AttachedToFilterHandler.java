package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.zone.CardInPlayComponent;

public class AttachedToFilterHandler extends CardFilterSystem {
    private CardFilteringSystem cardFilteringSystem;
    private IdProviderSystem idProviderSystem;

    public AttachedToFilterHandler() {
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
                        Entity attachedToEntity = idProviderSystem.getEntityById(attachedToId);
                        CardFilter filter = cardFilteringSystem.createAndFilter(parameters);
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
            cardFilteringSystem.validateFilter(parameter);
        }
    }
}
