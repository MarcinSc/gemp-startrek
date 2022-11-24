package com.gempukku.startrek.common;

import com.artemis.BaseSystem;
import com.artemis.Entity;

public class IdProviderSystem extends BaseSystem {
    private IdProvider idProvider;

    public IdProviderSystem(IdProvider idProvider) {
        this.idProvider = idProvider;
    }

    @Override
    protected void initialize() {
        idProvider.initialize(world);
    }

    public Entity getEntityById(String entityId) {
        return idProvider.getEntityById(entityId);
    }

    public String getEntityId(Entity entity) {
        return idProvider.getEntityId(entity);
    }

    @Override
    protected void processSystem() {

    }
}
