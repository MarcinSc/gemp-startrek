package com.gempukku.libgdx.lib.artemis.texture;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluableProperty;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluateProperty;
import com.gempukku.libgdx.lib.artemis.event.EventListener;

public class TextureSystem extends BaseSystem {
    private TextureHandler defaultTextureHandler;
    private final ObjectMap<String, TextureHandler> configuredTextureHandler = new ObjectMap<>();

    public void setDefaultTextureHandler(TextureHandler defaultTextureHandler) {
        this.defaultTextureHandler = defaultTextureHandler;
    }

    public void addTextureHandler(String atlas, TextureHandler textureHandler) {
        configuredTextureHandler.put(atlas, textureHandler);
    }

    public TextureRegion getTextureRegion(String atlas, String region) {
        TextureHandler textureHandler = configuredTextureHandler.get(atlas);
        if (textureHandler == null)
            textureHandler = defaultTextureHandler;
        return textureHandler.getTextureRegion(atlas, region);
    }

    @EventListener
    public void evaluateTextureReference(EvaluateProperty evaluateProperty, Entity entity) {
        EvaluableProperty propertyValue = evaluateProperty.getPropertyValue();
        if (propertyValue instanceof TextureReference) {
            TextureReference textureReference = (TextureReference) propertyValue;
            TextureRegion textureRegion = getTextureRegion(textureReference.getAtlas(), textureReference.getRegion());
            evaluateProperty.setResult(textureRegion);
        }
    }

    @Override
    protected void processSystem() {

    }
}
