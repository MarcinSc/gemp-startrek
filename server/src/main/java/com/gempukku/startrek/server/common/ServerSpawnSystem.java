package com.gempukku.startrek.server.common;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.gempukku.libgdx.artemis.template.ArtemisGameStateSerializer;
import com.gempukku.libgdx.artemis.template.ArtemisTemplateEntityLoader;

public class ServerSpawnSystem extends BaseSystem {
    private FileHandleResolver fileHandleResolver = new InternalFileHandleResolver();

    public void spawnEntities(String internalFilePath) {
        if (internalFilePath.endsWith(".entities")) {
            ArtemisGameStateSerializer.loadIntoEngine(world, internalFilePath, fileHandleResolver);
        }
    }

    public Entity spawnEntity(String internalFilePath) {
        if (internalFilePath.endsWith(".template")) {
            return ArtemisTemplateEntityLoader.loadTemplateToWorld(world, internalFilePath, fileHandleResolver);
        }
        return null;
    }

    @Override
    protected void processSystem() {

    }
}
