package com.gempukku.libgdx.lib.artemis.texture;

import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public interface TextureHandler extends Disposable {
    void setupWithWorld(World world);

    TextureRegion getTextureRegion(String path, String region);

    void update(float deltaTime);
}
