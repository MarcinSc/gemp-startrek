package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.IntMap;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.WritablePropertyContainer;
import com.gempukku.libgdx.graph.plugin.models.GraphModels;
import com.gempukku.libgdx.graph.util.sprite.MultiPageSpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModelProducer;
import com.gempukku.libgdx.graph.util.sprite.TexturePagedSpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.manager.MinimumSpriteRenderableModelManager;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.font.BitmapFontSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformUpdated;
import com.gempukku.libgdx.lib.graph.artemis.renderer.PipelineRendererSystem;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout.DefaultGlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout.GlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.DefaultTextParser;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextParser;

public class SDF3DTextSystem extends BaseEntitySystem {
    private BitmapFontSystem bitmapFontSystem;
    private TransformSystem transformSystem;
    private PipelineRendererSystem pipelineRendererSystem;
    private ComponentMapper<SDF3DTextComponent> sdf3DTextComponentMapper;

    private final IntMap<SDFText> renderedTexts = new IntMap<>();

    private SpriteBatchModel spriteBatchModel;

    private final GlyphOffseter glyphOffseter = new DefaultGlyphOffseter();
    private TextParser textParser;
    private final String sdfTextTag;

    public SDF3DTextSystem(String sdfTextTag) {
        this(sdfTextTag, new DefaultTextParser());
    }

    public SDF3DTextSystem(String sdfTextTag, TextParser textParser) {
        super(Aspect.all(SDF3DTextComponent.class));
        this.sdfTextTag = sdfTextTag;
        this.textParser = textParser;
    }

    public void setTextParser(TextParser textParser) {
        this.textParser = textParser;
    }

    private void initializeSpriteBatchModel() {
        GraphModels graphModels = pipelineRendererSystem.getPluginData(GraphModels.class);
        spriteBatchModel = new TexturePagedSpriteBatchModel(graphModels, sdfTextTag,
                new SpriteBatchModelProducer() {
                    @Override
                    public SpriteBatchModel create(WritablePropertyContainer writablePropertyContainer) {
                        return new MultiPageSpriteBatchModel(
                                new MinimumSpriteRenderableModelManager(1, false, 1024, graphModels, sdfTextTag),
                                writablePropertyContainer);
                    }
                });
    }

    @Override
    protected void inserted(int entityId) {
        if (spriteBatchModel == null) {
            initializeSpriteBatchModel();
        }
        Entity textEntity = world.getEntity(entityId);
        Matrix4 resolvedTransform = transformSystem.getResolvedTransform(textEntity);

        SDFText text = new SDFText(glyphOffseter, textParser, spriteBatchModel, bitmapFontSystem,
                resolvedTransform, sdf3DTextComponentMapper.get(entityId));

        renderedTexts.put(entityId, text);
    }

    @EventListener
    public void transformChanged(TransformUpdated transformUpdated, Entity entity) {
        SDFText text = renderedTexts.get(entity.getId());
        if (text != null)
            text.updateSprites();
    }

    public void updateSDFText(int entityId) {
        renderedTexts.get(entityId).updateSprites();
    }

    @Override
    protected void removed(int entityId) {
        SDFText text = renderedTexts.remove(entityId);
        text.dispose();
    }

    @Override
    protected void processSystem() {

    }
}
