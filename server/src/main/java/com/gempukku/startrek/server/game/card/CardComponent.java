package com.gempukku.startrek.server.game.card;

import com.artemis.Component;

public class CardComponent extends Component {
    private String cardId;
    private CardZone cardZone;
    private String owner;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public CardZone getCardZone() {
        return cardZone;
    }

    public void setCardZone(CardZone cardZone) {
        this.cardZone = cardZone;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
