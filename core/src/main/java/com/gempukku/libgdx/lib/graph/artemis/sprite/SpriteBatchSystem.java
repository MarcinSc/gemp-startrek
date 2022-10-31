package com.gempukku.libgdx.lib.graph.artemis.sprite;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.WritablePropertyContainer;
import com.gempukku.libgdx.graph.plugin.models.GraphModels;
import com.gempukku.libgdx.graph.util.sprite.MultiPageSpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModelProducer;
import com.gempukku.libgdx.graph.util.sprite.TexturePagedSpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.manager.MinimumSpriteRenderableModelManager;
import com.gempukku.libgdx.lib.artemis.evaluate.EvaluatePropertySystem;
import com.gempukku.libgdx.lib.graph.artemis.renderer.PipelineRendererSystem;

public class SpriteBatchSystem extends BaseEntitySystem {
    private EvaluatePropertySystem evaluatePropertySystem;
    private PipelineRendererSystem pipelineRendererSystem;

    private ComponentMapper<SpriteBatchComponent> spriteBatchComponentMapper;

    private final ObjectMap<String, SpriteBatchModel> spriteBatchMap = new ObjectMap<>();

    public SpriteBatchSystem() {
        super(Aspect.all(SpriteBatchComponent.class));
    }

    public SpriteBatchModel getSpriteBatchModel(String spriteBatchName) {
        return spriteBatchMap.get(spriteBatchName);
    }

    @Override
    protected void inserted(int entityId) {
        SpriteBatchComponent spriteSystem = spriteBatchComponentMapper.get(entityId);
        GraphModels graphModels = pipelineRendererSystem.getPluginData(GraphModels.class);

        String tag = spriteSystem.getRenderTag();
        SpriteBatchModel spriteModel = createSpriteBatchModel(spriteSystem, graphModels, tag);

        Entity entity = world.getEntity(entityId);
        WritablePropertyContainer propertyContainer = spriteModel.getPropertyContainer();
        for (ObjectMap.Entry<String, Object> property : spriteSystem.getProperties()) {
            propertyContainer.setValue(property.key, evaluatePropertySystem.evaluateProperty(entity, property.value, Object.class));
        }

        spriteBatchMap.put(spriteSystem.getName(), spriteModel);
    }

    private SpriteBatchModel createSpriteBatchModel(SpriteBatchComponent spriteSystem, GraphModels graphModels, String tag) {
        SpriteBatchComponent.SystemType spriteSystemType = spriteSystem.getType();
        if (spriteSystemType == SpriteBatchComponent.SystemType.TexturePaged) {
            return new TexturePagedSpriteBatchModel(graphModels, tag,
                    new SpriteBatchModelProducer() {
                        @Override
                        public SpriteBatchModel create(WritablePropertyContainer writablePropertyContainer) {
                            return new MultiPageSpriteBatchModel(
                                    new MinimumSpriteRenderableModelManager(
                                            spriteSystem.getMinimumPages(), spriteSystem.isStaticBatch(), spriteSystem.getSpritesPerPage(),
                                            graphModels, tag), writablePropertyContainer);
                        }
                    });
        } else if (spriteSystemType == SpriteBatchComponent.SystemType.MultiPaged) {
            return new MultiPageSpriteBatchModel(
                    new MinimumSpriteRenderableModelManager(
                            spriteSystem.getMinimumPages(), spriteSystem.isStaticBatch(), spriteSystem.getSpritesPerPage(),
                            graphModels, tag));
        } else {
            throw new GdxRuntimeException("Unable to create SpriteBatchModel unknown type: " + spriteSystemType);
        }
    }

    @Override
    protected void removed(int entityId) {
        SpriteBatchComponent spriteSystemComponent = spriteBatchComponentMapper.get(entityId);
        SpriteBatchModel spritesModel = spriteBatchMap.remove(spriteSystemComponent.getName());
        spritesModel.dispose();
    }

    @Override
    protected void processSystem() {

    }

    @Override
    public void dispose() {
        for (SpriteBatchModel spriteModel : spriteBatchMap.values()) {
            spriteModel.dispose();
        }
        spriteBatchMap.clear();
    }
}
