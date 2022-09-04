package com.gempukku.libgdx.lib.artemis.texture;

import com.artemis.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class RuntimeTextureHandler implements TextureHandler {
    private ObjectMap<String, TextureAtlas> loadedTextures = new ObjectMap<>();

    @Override
    public void setupWithWorld(World world) {

    }

    @Override
    public TextureRegion getTextureRegion(String path, String region) {
        TextureAtlas textureAtlas = loadedTextures.get(path);
        if (textureAtlas == null) {
            if (path.endsWith(".atlas")) {
                textureAtlas = new TextureAtlas(path);
                loadedTextures.put(path, textureAtlas);
            } else {
                textureAtlas = new TextureAtlas();
                Texture texture = new Texture(path);
                textureAtlas.addRegion(region, texture, 0, 0, texture.getWidth(), texture.getHeight());
                loadedTextures.put(path, textureAtlas);
            }
        }

        return textureAtlas.findRegion(region);
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void dispose() {
        for (TextureAtlas value : loadedTextures.values()) {
            value.dispose();
        }
        loadedTextures.clear();
    }
}
