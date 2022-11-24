package com.gempukku.startrek.common;

import com.artemis.Entity;
import com.artemis.World;

public interface IdProvider {
    void initialize(World world);

    String getEntityId(Entity entity);

    Entity getEntityById(String entityId);
}
