package com.gempukku.startrek.game.zone;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.network.server.config.annotation.SendToClients;

@SendToClients
public class CardChangedZones implements EntityEvent {
    private CardZone previousZone;

    public CardChangedZones() {
    }

    public CardChangedZones(CardZone previousZone) {
        this.previousZone = previousZone;
    }

    public CardZone getPreviousZone() {
        return previousZone;
    }
}
