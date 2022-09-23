package com.gempukku.startrek.server.common;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.artemis.template.ArtemisTemplateEntityLoader;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.template.JsonTemplateLoader;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerSpawnSystem extends BaseSystem {
    private HierarchySystem hierarchySystem;
    private TransformSystem transformSystem;

    public Entity spawnEntity(String internalFilePath) {
        if (internalFilePath.endsWith(".template")) {
            ClassPathResource classPathResource = new ClassPathResource(internalFilePath);
            try (InputStream inputStream = classPathResource.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
                JsonValue jsonValue = JsonTemplateLoader.loadTemplateFromFile(reader, null);
                return ArtemisTemplateEntityLoader.loadArtemisTemplateToWorld(world, jsonValue);
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void processSystem() {

    }
}
