package com.gempukku.startrek.card;

import com.artemis.BaseSystem;

public class CardLookupSystem extends BaseSystem {
    private CardData cardData;

    public CardLookupSystem(CardData cardData) {
        this.cardData = cardData;
    }

    public CardDefinition getCardDefinition(String cardId) {
        return cardData.getCardDefinition(cardId);
    }

    @Override
    protected void processSystem() {

    }
}
