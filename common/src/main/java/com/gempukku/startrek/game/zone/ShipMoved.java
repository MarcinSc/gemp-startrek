package com.gempukku.startrek.game.zone;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.network.server.config.annotation.SendToClients;

@SendToClients
public class ShipMoved implements EntityEvent {
    private String shipId;
    private Array<String> entitiesOnBoard;

    public ShipMoved() {

    }

    public ShipMoved(String shipId, Array<String> entitiesOnBoard) {
        this.shipId = shipId;
        this.entitiesOnBoard = entitiesOnBoard;
    }

    public String getShipId() {
        return shipId;
    }
}
