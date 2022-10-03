package com.gempukku.libgdx.lib.graph.artemis.sprite;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.PropertyContainer;
import com.gempukku.libgdx.graph.shader.property.MapWritablePropertyContainer;
import com.gempukku.libgdx.graph.util.sprite.RenderableSprite;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluatePropertySystem;

public class SpriteComponentAdapter implements Disposable {
    private static final Vector3 position = new Vector3();

    private final EvaluatePropertySystem propertySystem;
    private final MapWritablePropertyContainer propertyContainer;
    private final SpriteBatchModel spriteBatchModel;
    private final SpriteComponent spriteComponent;
    private final Entity entity;

    private final Sprite3DRenderableSprite renderableSprite;

    public SpriteComponentAdapter(EvaluatePropertySystem propertySystem, SpriteBatchModel spriteBatchModel,
                                  SpriteComponent spriteComponent, Entity entity) {
        this.propertySystem = propertySystem;
        this.spriteBatchModel = spriteBatchModel;
        this.spriteComponent = spriteComponent;
        this.entity = entity;

        renderableSprite = new Sprite3DRenderableSprite();

        this.propertyContainer = new MapWritablePropertyContainer();

        evaluateProperties();

        spriteBatchModel.addSprite(renderableSprite);
    }

    private void evaluateProperties() {
        propertyContainer.clear();

        for (ObjectMap.Entry<String, Object> property : spriteComponent.getProperties()) {
            propertyContainer.setValue(property.key, propertySystem.evaluateProperty(entity, property.value, Object.class));
        }
    }

    public void updateSprite() {
        evaluateProperties();
        spriteBatchModel.updateSprite(renderableSprite);
    }

    @Override
    public void dispose() {
        spriteBatchModel.removeSprite(renderableSprite);
    }

    private class Sprite3DRenderableSprite implements RenderableSprite {
        @Override
        public Vector3 getPosition() {
            return position;
        }

        @Override
        public boolean isRendered(Camera camera) {
            return true;
        }

        @Override
        public PropertyContainer getPropertyContainer() {
            return propertyContainer;
        }

        @Override
        public void setUnknownPropertyInAttribute(VertexAttribute vertexAttribute, float[] floats, int i) {

        }
    }
}
