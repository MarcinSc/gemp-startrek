package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.zone.CardZone;

public class CardZoneFilterHandler extends CardFilterSystem {
    private AmountResolverSystem amountResolverSystem;
    private CardLookupSystem cardLookupSystem;

    public CardZoneFilterHandler() {
        super("zone");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        CardZone cardZone = CardZone.valueOf(parameters.get(0));
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                CardComponent card = cardEntity.getComponent(CardComponent.class);
                return card.getCardZone() == cardZone;
            }
        };
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        CardZone.valueOf(parameters.get(0));
    }
}
