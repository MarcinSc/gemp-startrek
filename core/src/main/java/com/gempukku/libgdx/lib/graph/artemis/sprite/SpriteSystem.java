package com.gempukku.libgdx.lib.graph.artemis.sprite;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.WritablePropertyContainer;
import com.gempukku.libgdx.graph.plugin.models.GraphModels;
import com.gempukku.libgdx.graph.shader.property.MapWritablePropertyContainer;
import com.gempukku.libgdx.graph.util.ValueOperations;
import com.gempukku.libgdx.graph.util.sprite.MultiPageSpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModelProducer;
import com.gempukku.libgdx.graph.util.sprite.TexturePagedSpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.manager.MinimumSpriteRenderableModelManager;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluableProperty;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluateProperty;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluatePropertySystem;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformUpdated;
import com.gempukku.libgdx.lib.graph.artemis.Vector2ValuePerVertex;
import com.gempukku.libgdx.lib.graph.artemis.Vector3ValuePerVertex;
import com.gempukku.libgdx.lib.graph.artemis.renderer.PipelineRendererSystem;

public class SpriteSystem extends BaseSystem implements Disposable {
    private final ObjectMap<String, TexturePagedSpriteBatchModel> spriteSystemMap = new ObjectMap<>();
    private final IntMap<Array<SpriteDefinitionAdapter>> spriteMap = new IntMap<>();

    private ComponentMapper<SpriteComponent> spriteComponentMapper;
    private ComponentMapper<SpriteSystemComponent> spriteSystemComponentMapper;

    private PipelineRendererSystem pipelineRendererSystem;
    private TransformSystem transformSystem;
    private EvaluatePropertySystem evaluatePropertySystem;
    private EvaluatePropertySystem propertySystem;

    private static final Vector2ValuePerVertex uvAttribute = new Vector2ValuePerVertex(new float[]{0, 0, 1, 0, 0, 1, 1, 1});

    private final Matrix4 tempMatrix = new Matrix4();
    private final Vector3 tempVector3 = new Vector3();

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(SpriteSystemComponent.class)).
                addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; i++) {
                                    int entityId = entities.get(i);
                                    spriteSystemInserted(entityId);
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; i++) {
                                    int entityId = entities.get(i);
                                    spriteSystemRemoved(entityId);
                                }
                            }
                        }
                );

        world.getAspectSubscriptionManager().get(Aspect.all(SpriteComponent.class)).
                addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; i++) {
                                    int entityId = entities.get(i);
                                    spriteInserted(entityId);
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; i++) {
                                    int entityId = entities.get(i);
                                    spriteRemoved(entityId);
                                }
                            }
                        }
                );
    }

    public void updateSprite(int entityId) {
        Entity spriteEntity = world.getEntity(entityId);
        SpriteComponent sprite = spriteEntity.getComponent(SpriteComponent.class);

        for (SpriteDefinitionAdapter spriteDefinitionAdapter : spriteMap.remove(entityId)) {
            spriteDefinitionAdapter.dispose();
        }

        addSprites(entityId, spriteEntity, sprite);
    }

    @EventListener
    public void transformUpdated(TransformUpdated transformUpdated, Entity entity) {
        int entityId = entity.getId();
        if (spriteMap.containsKey(entityId))
            updateSprite(entity.getId());
    }

    @EventListener
    public void evaluateSpritePosition(EvaluateProperty evaluateProperty, Entity entity) {
        EvaluableProperty propertyValue = evaluateProperty.getPropertyValue();
        if (propertyValue instanceof SpritePositionProperty) {
            SpritePositionProperty spritePositionProperty = (SpritePositionProperty) propertyValue;

            Matrix4 transform = transformSystem.getResolvedTransform(entity);
            Matrix4 resultTransform = tempMatrix.set(transform).mul(spritePositionProperty.getTransform());
            Vector3 rightVector = spritePositionProperty.getRightVector();
            Vector3 upVector = spritePositionProperty.getUpVector();

            float[] positionAttributeFloatArray = new float[4 * 3];

            tempVector3.setZero()
                    .mulAdd(rightVector, -0.5f)
                    .mulAdd(upVector, -0.5f).mul(resultTransform);
            ValueOperations.copyVector3IntoArray(tempVector3, positionAttributeFloatArray, 0);
            tempVector3.setZero()
                    .mulAdd(rightVector, 0.5f)
                    .mulAdd(upVector, -0.5f).mul(resultTransform);
            ValueOperations.copyVector3IntoArray(tempVector3, positionAttributeFloatArray, 3);
            tempVector3.setZero()
                    .mulAdd(rightVector, -0.5f)
                    .mulAdd(upVector, 0.5f).mul(resultTransform);
            ValueOperations.copyVector3IntoArray(tempVector3, positionAttributeFloatArray, 6);
            tempVector3.setZero()
                    .mulAdd(rightVector, 0.5f)
                    .mulAdd(upVector, 0.5f).mul(resultTransform);
            ValueOperations.copyVector3IntoArray(tempVector3, positionAttributeFloatArray, 9);

            Vector3ValuePerVertex positionPerVertex = new Vector3ValuePerVertex(positionAttributeFloatArray);
            evaluateProperty.setResult(positionPerVertex);
        }
    }

    @EventListener
    public void evaluateSpriteUV(EvaluateProperty evaluateProperty, Entity entity) {
        EvaluableProperty propertyValue = evaluateProperty.getPropertyValue();
        if (propertyValue instanceof SpriteUVProperty) {
            evaluateProperty.setResult(uvAttribute);
        }
    }

    private void spriteSystemInserted(int entityId) {
        SpriteSystemComponent spriteSystemComponent = spriteSystemComponentMapper.get(entityId);
        GraphModels graphModels = pipelineRendererSystem.getPluginData(GraphModels.class);

        String tag = spriteSystemComponent.getRenderTag();
        TexturePagedSpriteBatchModel spriteModel = new TexturePagedSpriteBatchModel(graphModels, tag,
                new SpriteBatchModelProducer() {
                    @Override
                    public SpriteBatchModel create(WritablePropertyContainer writablePropertyContainer) {
                        return new MultiPageSpriteBatchModel(
                                new MinimumSpriteRenderableModelManager(1, false, spriteSystemComponent.getSpritesPerPage(),
                                        graphModels, tag), writablePropertyContainer);
                    }
                });

        Entity entity = world.getEntity(entityId);
        WritablePropertyContainer propertyContainer = spriteModel.getPropertyContainer();
        for (ObjectMap.Entry<String, Object> property : spriteSystemComponent.getProperties()) {
            propertyContainer.setValue(property.key, evaluatePropertySystem.evaluateProperty(entity, property.value, Object.class));
        }

        spriteSystemMap.put(spriteSystemComponent.getName(), spriteModel);
    }

    private void spriteSystemRemoved(int entityId) {
        SpriteSystemComponent spriteSystemComponent = spriteSystemComponentMapper.get(entityId);
        SpriteBatchModel spritesModel = spriteSystemMap.remove(spriteSystemComponent.getName());
        spritesModel.dispose();
    }

    private void spriteInserted(int entityId) {
        Entity spriteEntity = world.getEntity(entityId);
        SpriteComponent sprite = spriteComponentMapper.get(entityId);

        addSprites(entityId, spriteEntity, sprite);
    }

    private void addSprites(int entityId, Entity spriteEntity, SpriteComponent sprite) {
        MapWritablePropertyContainer propertyContainer = new MapWritablePropertyContainer();
        evaluatePropertyContainer(spriteEntity, sprite, propertyContainer);

        Array<SpriteDefinitionAdapter> spriteComponentAdapters = new Array<>();
        for (SpriteDefinition spriteDefinition : sprite.getSprites()) {
            TexturePagedSpriteBatchModel spriteBatchModel = spriteSystemMap.get(spriteDefinition.getSpriteSystemName());
            SpriteDefinitionAdapter spriteComponentAdapter = new SpriteDefinitionAdapter(propertyContainer, evaluatePropertySystem,
                    spriteBatchModel, spriteDefinition, spriteEntity);
            spriteComponentAdapters.add(spriteComponentAdapter);
        }

        spriteMap.put(entityId, spriteComponentAdapters);
    }

    private void evaluatePropertyContainer(Entity entity, SpriteComponent spriteComponent, MapWritablePropertyContainer propertyContainer) {
        for (ObjectMap.Entry<String, Object> property : spriteComponent.getProperties()) {
            propertyContainer.setValue(property.key, propertySystem.evaluateProperty(entity, property.value, Object.class));
        }
    }

    private void spriteRemoved(int entityId) {
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
        for (SpriteBatchModel spriteModel : spriteSystemMap.values()) {
            spriteModel.dispose();
        }
        spriteSystemMap.clear();
        spriteMap.clear();
    }
}
