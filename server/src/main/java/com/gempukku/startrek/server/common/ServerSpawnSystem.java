package com.gempukku.startrek.server.common;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.artemis.template.ArtemisGameStateSerializer;
import com.gempukku.libgdx.artemis.template.ArtemisTemplateEntityLoader;
import com.gempukku.libgdx.template.JsonTemplateLoader;

public class ServerSpawnSystem extends BaseSystem {
    private FileHandleResolver fileHandleResolver = new InternalFileHandleResolver();
    private ObjectMap<String, JsonValue> cachedJsons = new ObjectMap<>();

    public void spawnEntities(String internalFilePath) {
        if (internalFilePath.endsWith(".entities")) {
            ArtemisGameStateSerializer.loadIntoEngine(world, internalFilePath, fileHandleResolver);
        }
    }

    public Entity spawnEntity(String internalFilePath) {
        System.out.println("Spawning entity: " + internalFilePath);
        if (internalFilePath.endsWith(".template")) {
            return ArtemisTemplateEntityLoader.loadArtemisTemplateToWorld(world, loadJson(internalFilePath));
        }
        return null;
    }

    private JsonValue loadJson(String internalFilePath) {
        JsonValue result = cachedJsons.get(internalFilePath);
        if (result == null) {
            result = JsonTemplateLoader.loadTemplateFromFile(internalFilePath, fileHandleResolver);
            cachedJsons.put(internalFilePath, result);
        }
        return result;
    }

    @Override
    protected void processSystem() {

    }
}
