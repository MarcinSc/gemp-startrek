package com.gempukku.startrek;

import com.artemis.Entity;
import com.artemis.World;
import com.gempukku.startrek.common.IdProvider;
import com.gempukku.startrek.common.IncomingUpdatesProcessor;

public class ClientIdProvider implements IdProvider {
    private IncomingUpdatesProcessor incomingUpdatesProcessor;

    @Override
    public void initialize(World world) {
        incomingUpdatesProcessor = world.getSystem(IncomingUpdatesProcessor.class);
    }

    @Override
    public String getEntityId(Entity entity) {
        return incomingUpdatesProcessor.getEntityId(entity);
    }

    @Override
    public Entity getEntityById(String entityId) {
        return incomingUpdatesProcessor.getEntityById(entityId);
    }
}
