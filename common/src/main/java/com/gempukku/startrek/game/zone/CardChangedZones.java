package com.gempukku.startrek.game.zone;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.network.server.config.annotation.SendToClients;

@SendToClients
public class CardChangedZones implements EntityEvent {
    private String cardId;
    private String cardOwner;
    private CardZone fromZone;
    private CardZone toZone;
    private String missionOwner;
    private int missionIndex;

    public CardChangedZones() {
    }

    public CardChangedZones(String cardId, String cardOwner,
                            CardZone fromZone, CardZone toZone,
                            String missionOwner, int missionIndex) {
        this.cardId = cardId;
        this.cardOwner = cardOwner;
        this.fromZone = fromZone;
        this.toZone = toZone;
        this.missionOwner = missionOwner;
        this.missionIndex = missionIndex;
    }

    public String getCardId() {
        return cardId;
    }

    public String getCardOwner() {
        return cardOwner;
    }

    public CardZone getFromZone() {
        return fromZone;
    }

    public CardZone getToZone() {
        return toZone;
    }

    public String getMissionOwner() {
        return missionOwner;
    }

    public int getMissionIndex() {
        return missionIndex;
    }
}
