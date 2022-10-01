package com.gempukku.startrek.server.game.card;

import com.artemis.BaseSystem;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.card.CardDefinition;

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
