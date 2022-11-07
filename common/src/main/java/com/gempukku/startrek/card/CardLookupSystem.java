package com.gempukku.startrek.card;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.gempukku.startrek.game.CardComponent;

public class CardLookupSystem extends BaseSystem {
    private final CardData cardData;

    public CardLookupSystem(CardData cardData) {
        this.cardData = cardData;
    }

    public CardDefinition getCardDefinition(String cardId) {
        return cardData.getCardDefinition(cardId);
    }

    public CardDefinition getCardDefinition(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        return getCardDefinition(card.getCardId());
    }

    public CardData getCardData() {
        return cardData;
    }

    @Override
    protected void processSystem() {

    }
}
