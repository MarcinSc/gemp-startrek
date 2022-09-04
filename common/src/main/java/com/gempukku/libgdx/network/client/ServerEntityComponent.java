package com.gempukku.libgdx.network.client;

import com.artemis.Component;

public class ServerEntityComponent extends Component {
    private int entityId;

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
