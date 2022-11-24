package com.gempukku.startrek.server;

import com.artemis.Entity;
import com.artemis.World;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.common.IdProvider;

public class ServerIdProvider implements IdProvider {
    private ServerEntityIdSystem serverEntityIdSystem;

    @Override
    public void initialize(World world) {
        serverEntityIdSystem = world.getSystem(ServerEntityIdSystem.class);
    }

    @Override
    public String getEntityId(Entity entity) {
        return serverEntityIdSystem.getEntityId(entity);
    }

    @Override
    public Entity getEntityById(String entityId) {
        return serverEntityIdSystem.findfromId(entityId);
    }
}
