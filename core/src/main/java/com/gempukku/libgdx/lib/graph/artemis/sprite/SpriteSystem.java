package com.gempukku.libgdx.lib.graph.artemis.sprite;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.PropertyContainer;
import com.gempukku.libgdx.graph.shader.property.MapWritablePropertyContainer;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluableProperty;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluatePropertySystem;
import com.gempukku.libgdx.lib.artemis.evaluate.PropertyEvaluator;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformUpdated;
import com.gempukku.libgdx.lib.graph.artemis.Vector2ValuePerVertex;
import com.gempukku.libgdx.lib.graph.artemis.VectorUtil;

public class SpriteSystem extends BaseEntitySystem implements PropertyEvaluator {
    private final IntMap<Array<SpriteDefinitionAdapter>> spriteMap = new IntMap<>();

    private ComponentMapper<SpriteComponent> spriteComponentMapper;

    private SpriteBatchSystem spriteBatchSystem;
    private TransformSystem transformSystem;
    private EvaluatePropertySystem evaluatePropertySystem;

    public static final Vector2ValuePerVertex uvAttribute = new Vector2ValuePerVertex(new float[]{0, 0, 1, 0, 0, 1, 1, 1});

    private final Matrix4 tempMatrix = new Matrix4();

    public SpriteSystem() {
        super(Aspect.all(SpriteComponent.class));
    }

    @Override
    protected void initialize() {
        evaluatePropertySystem.addPropertyEvaluator(this);
    }

    public void updateSprites(int entityId) {
        Entity spriteEntity = world.getEntity(entityId);
        SpriteComponent sprite = spriteEntity.getComponent(SpriteComponent.class);

        for (SpriteDefinitionAdapter spriteDefinitionAdapter : spriteMap.remove(entityId)) {
            spriteDefinitionAdapter.dispose();
        }

        addSprites(entityId, spriteEntity, sprite);
    }

    public void updateSprite(int entityId, int spriteDefinitionIndex) {
        Entity spriteEntity = world.getEntity(entityId);
        SpriteComponent sprite = spriteEntity.getComponent(SpriteComponent.class);
        Array<SpriteDefinitionAdapter> spriteDefinitionAdapters = spriteMap.get(entityId);
        if (spriteDefinitionAdapters != null) {
            spriteDefinitionAdapters.get(spriteDefinitionIndex).updateSprite(sprite.getSprites().get(spriteDefinitionIndex));
        }
    }

    @EventListener
    public void transformUpdated(TransformUpdated transformUpdated, Entity entity) {
        int entityId = entity.getId();
        if (spriteMap.containsKey(entityId))
            updateSprites(entity.getId());
    }

    @Override
    public boolean evaluatesProperty(Entity entity, EvaluableProperty value) {
        return value instanceof SpritePositionProperty
                || value instanceof SpriteUVProperty;
    }

    @Override
    public Object evaluateValue(Entity entity, EvaluableProperty value) {
        if (value instanceof SpritePositionProperty) {
            SpritePositionProperty spritePositionProperty = (SpritePositionProperty) value;

            Matrix4 transform = transformSystem.getResolvedTransform(entity);

            Matrix4 resultTransform = tempMatrix.set(transform).mul(spritePositionProperty.getTransform());
            Vector3 rightVector = spritePositionProperty.getRightVector();
            Vector3 upVector = spritePositionProperty.getUpVector();

            return VectorUtil.createCenterSpritePosition(1f, 1f, rightVector, upVector, resultTransform);
        } else {
            return uvAttribute;
        }
    }

    @Override
    protected void inserted(int entityId) {
        Entity spriteEntity = world.getEntity(entityId);
        SpriteComponent sprite = spriteComponentMapper.get(entityId);

        addSprites(entityId, spriteEntity, sprite);
    }

    private void addSprites(int entityId, Entity spriteEntity, SpriteComponent sprite) {
        MapWritablePropertyContainer propertyContainer = new MapWritablePropertyContainer();
        evaluatePropertyContainer(spriteEntity, sprite, propertyContainer);

        Array<SpriteDefinitionAdapter> spriteComponentAdapters = new Array<>();
        for (SpriteDefinition spriteDefinition : sprite.getSprites()) {
            SpriteDefinitionAdapter spriteComponentAdapter = addSprite(spriteEntity, propertyContainer, spriteDefinition);
            spriteComponentAdapters.add(spriteComponentAdapter);
        }

        spriteMap.put(entityId, spriteComponentAdapters);
    }

    public SpriteDefinitionAdapter addSprite(Entity spriteEntity, PropertyContainer propertyContainer, SpriteDefinition spriteDefinition) {
        SpriteBatchModel spriteBatchModel = spriteBatchSystem.getSpriteBatchModel(spriteDefinition.getSpriteBatchName());
        return new SpriteDefinitionAdapter(propertyContainer, evaluatePropertySystem,
                spriteBatchModel, spriteDefinition, spriteEntity);
    }

    public void removeSprite(SpriteDefinitionAdapter spriteDefinitionAdapter) {
        spriteDefinitionAdapter.dispose();
    }

    private void evaluatePropertyContainer(Entity entity, SpriteComponent spriteComponent, MapWritablePropertyContainer propertyContainer) {
        for (ObjectMap.Entry<String, Object> property : spriteComponent.getProperties()) {
            propertyContainer.setValue(property.key, evaluatePropertySystem.evaluateProperty(entity, property.value, Object.class));
        }
    }


    @Override
    protected void removed(int entityId) {
        Array<SpriteDefinitionAdapter> sprites = spriteMap.remove(entityId);
        for (SpriteDefinitionAdapter sprite : sprites) {
            sprite.dispose();
        }
    }

    @Override
    protected void processSystem() {

    }

    @Override
    public void dispose() {
        for (Array<SpriteDefinitionAdapter> spriteArray : spriteMap.values()) {
            for (SpriteDefinitionAdapter spriteDefinitionAdapter : spriteArray) {
                spriteDefinitionAdapter.dispose();
            }
        }
        spriteMap.clear();
    }
}
