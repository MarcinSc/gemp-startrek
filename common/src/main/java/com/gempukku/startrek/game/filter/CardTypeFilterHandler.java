package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class CardTypeFilterHandler extends CardFilterSystem {
    private CardLookupSystem cardLookupSystem;

    public CardTypeFilterHandler() {
        super("type");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        for (CardType value : CardType.values()) {
            if (value.name().equalsIgnoreCase(parameters.get(0)))
                return new TypeCardFilter(value);
        }

        throw new GdxRuntimeException("Unable to find CardType: " + parameters.get(0));
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 1);
        for (CardType value : CardType.values()) {
            if (value.name().equalsIgnoreCase(parameters.get(0)))
                return;
        }

        throw new GdxRuntimeException("Unable to find CardType: " + parameters.get(0));
    }

    private class TypeCardFilter implements CardFilter {
        private CardType cardType;

        public TypeCardFilter(CardType cardType) {
            this.cardType = cardType;
        }

        @Override
        public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity.getComponent(CardComponent.class).getCardId());
            return cardDefinition.getType() == cardType;
        }
    }
}
