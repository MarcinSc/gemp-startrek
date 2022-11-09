package com.gempukku.startrek.game;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateWithOthers;
import com.gempukku.startrek.game.zone.CardZone;

@ReplicateWithOthers
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
