package com.gempukku.startrek.game.event;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.network.server.config.annotation.SendToClients;

@SendToClients
public class ShipMoved implements EntityEvent {
    private String shipId;
    private String missionOwnerFrom;
    private int missionIndexFrom;
    private String missionOwnerTo;
    private int missionIndexTo;

    public ShipMoved() {

    }

    public ShipMoved(String shipId, String missionOwnerFrom, int missionIndexFrom, String missionOwnerTo, int missionIndexTo) {
        this.shipId = shipId;
        this.missionOwnerFrom = missionOwnerFrom;
        this.missionIndexFrom = missionIndexFrom;
        this.missionOwnerTo = missionOwnerTo;
        this.missionIndexTo = missionIndexTo;
    }

    public String getShipId() {
        return shipId;
    }

    public String getMissionOwnerFrom() {
        return missionOwnerFrom;
    }

    public int getMissionIndexFrom() {
        return missionIndexFrom;
    }

    public String getMissionOwnerTo() {
        return missionOwnerTo;
    }

    public int getMissionIndexTo() {
        return missionIndexTo;
    }
}
