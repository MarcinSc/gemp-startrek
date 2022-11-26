package com.gempukku.startrek.game.zone;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.network.server.config.annotation.SendToClients;

@SendToClients
public class CardHidden implements EntityEvent {
    private String cardId;

    public CardHidden() {

    }

    public CardHidden(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }
}
