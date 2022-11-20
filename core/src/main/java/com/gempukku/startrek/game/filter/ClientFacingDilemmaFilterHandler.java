package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.id.ServerEntityIdComponent;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.FacedDilemmaComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class ClientFacingDilemmaFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;

    public ClientFacingDilemmaFilterHandler() {
        super("facingDilemma");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                if (cardLookupSystem.getCardDefinition(cardEntity).getType() != CardType.Personnel)
                    return false;

                Entity facedDilemmaEntity = LazyEntityUtil.findEntityWithComponent(world, FacedDilemmaComponent.class);
                if (facedDilemmaEntity == null)
                    return false;

                String cardEntityId = cardEntity.getComponent(ServerEntityIdComponent.class).getId();

                FacedDilemmaComponent facedDilemma = facedDilemmaEntity.getComponent(FacedDilemmaComponent.class);
                for (String entityId : facedDilemma.getFacingPersonnel()) {
                    if (entityId.equals(cardEntityId))
                        return true;
                }

                return false;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
