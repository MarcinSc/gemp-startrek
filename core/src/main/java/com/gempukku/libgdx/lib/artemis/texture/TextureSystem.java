package com.gempukku.libgdx.lib.artemis.texture;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluableProperty;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluateProperty;
import com.gempukku.libgdx.lib.artemis.event.EventListener;

public class TextureSystem extends BaseSystem implements Disposable {
    private TextureHandler textureHandler;

    public TextureSystem(TextureHandler textureHandler) {
        this.textureHandler = textureHandler;
    }

    @Override
    protected void initialize() {
        textureHandler.setupWithWorld(world);
    }

    public TextureRegion getTextureRegion(String path, String region) {
        return textureHandler.getTextureRegion(path, region);
    }

    @EventListener
    public void evaluateTextureReference(EvaluateProperty evaluateProperty, Entity entity) {
        EvaluableProperty propertyValue = evaluateProperty.getPropertyValue();
        if (propertyValue instanceof TextureReference) {
            TextureReference textureReference = (TextureReference) propertyValue;
            TextureRegion textureRegion = getTextureRegion(textureReference.getPath(), textureReference.getRegion());
            evaluateProperty.setResult(textureRegion);
        }
    }

    @Override
    protected void processSystem() {
        textureHandler.update(world.getDelta());
    }

    @Override
    public void dispose() {
        textureHandler.dispose();
    }
}
