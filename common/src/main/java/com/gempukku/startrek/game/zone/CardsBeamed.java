package com.gempukku.startrek.game.zone;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.network.server.config.annotation.SendToClients;

@SendToClients
public class CardsBeamed implements EntityEvent {
    private String fromShipId;
    private String toShipId;
    private Array<String> entityIds;

    public CardsBeamed() {

    }

    public CardsBeamed(String fromShipId, String toShipId, Array<String> entityIds) {
        this.fromShipId = fromShipId;
        this.toShipId = toShipId;
        this.entityIds = entityIds;
    }

    public String getFromShipId() {
        return fromShipId;
    }

    public String getToShipId() {
        return toShipId;
    }

    public Array<String> getEntityIds() {
        return entityIds;
    }
}
