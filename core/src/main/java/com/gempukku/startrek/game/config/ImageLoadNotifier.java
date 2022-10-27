package com.gempukku.startrek.game.config;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface ImageLoadNotifier {
    void textureLoaded(TextureRegion textureRegion);

    void textureError();
}
