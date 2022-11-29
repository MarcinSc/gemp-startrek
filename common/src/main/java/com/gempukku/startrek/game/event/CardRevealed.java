package com.gempukku.startrek.game.event;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.network.server.config.annotation.SendToClients;

@SendToClients
public class CardRevealed implements EntityEvent {
    private String cardId;

    public CardRevealed() {
    }

    public CardRevealed(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }
}
