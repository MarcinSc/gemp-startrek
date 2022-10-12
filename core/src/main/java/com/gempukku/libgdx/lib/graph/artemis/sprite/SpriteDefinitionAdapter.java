package com.gempukku.libgdx.lib.graph.artemis.sprite;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.PropertyContainer;
import com.gempukku.libgdx.graph.util.property.HierarchicalPropertyContainer;
import com.gempukku.libgdx.graph.util.sprite.RenderableSprite;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluatePropertySystem;

public class SpriteDefinitionAdapter implements Disposable {
    private static final Vector3 position = new Vector3();

    private final EvaluatePropertySystem propertySystem;
    private final HierarchicalPropertyContainer propertyContainer;
    private final SpriteBatchModel spriteBatchModel;
    private final Entity entity;

    private final Sprite3DRenderableSprite renderableSprite;

    public SpriteDefinitionAdapter(PropertyContainer parentProperties, EvaluatePropertySystem propertySystem,
                                   SpriteBatchModel spriteBatchModel, SpriteDefinition spriteDefintion, Entity entity) {
        this.propertySystem = propertySystem;
        this.spriteBatchModel = spriteBatchModel;
        this.entity = entity;

        renderableSprite = new Sprite3DRenderableSprite();

        this.propertyContainer = new HierarchicalPropertyContainer(parentProperties);

        evaluateProperties(spriteDefintion);

        spriteBatchModel.addSprite(renderableSprite);
    }

    private void evaluateProperties(SpriteDefinition spriteDefinition) {
        propertyContainer.clear();

        for (ObjectMap.Entry<String, Object> property : spriteDefinition.getProperties()) {
            propertyContainer.setValue(property.key, propertySystem.evaluateProperty(entity, property.value, Object.class));
        }
    }

    public void updateSprite(SpriteDefinition spriteDefinition) {
        evaluateProperties(spriteDefinition);
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
