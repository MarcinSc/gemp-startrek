package com.gempukku.startrek.game.config;

import com.artemis.BaseSystem;
import com.gempukku.libgdx.lib.artemis.texture.RuntimeTextureHandler;
import com.gempukku.libgdx.lib.artemis.texture.TextureSystem;

public class ConfigureTextureSystem extends BaseSystem {
    private TextureSystem textureSystem;

    private RuntimeTextureHandler defaultTextureHandler;

    @Override
    protected void initialize() {
        defaultTextureHandler = new RuntimeTextureHandler();
        textureSystem.setDefaultTextureHandler(defaultTextureHandler);


    }

    @Override
    protected void dispose() {
        defaultTextureHandler.dispose();
    }

    @Override
    protected void processSystem() {

    }
}
