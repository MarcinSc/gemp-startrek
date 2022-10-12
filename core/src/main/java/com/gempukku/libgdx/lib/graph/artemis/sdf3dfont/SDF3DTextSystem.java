package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.IntMap;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.WritablePropertyContainer;
import com.gempukku.libgdx.graph.plugin.models.GraphModels;
import com.gempukku.libgdx.graph.util.sprite.BasicSpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModelProducer;
import com.gempukku.libgdx.graph.util.sprite.TexturePagedSpriteBatchModel;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.font.BitmapFontSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformUpdated;

import java.util.function.Function;

public class SDF3DTextSystem extends BaseEntitySystem {
    private BitmapFontSystem bitmapFontSystem;
    private TransformSystem transformSystem;
    private ComponentMapper<SDF3DTextComponent> sdf3DTextComponentMapper;

    private final IntMap<SDFText> renderedTexts = new IntMap<>();
    private Function<String, BitmapFont> bitmapFontFunction;

    private SpriteBatchModel spriteBatchModel;

    public SDF3DTextSystem(GraphModels graphModels, String sdfTextGraphSpriteTag) {
        super(Aspect.all(SDF3DTextComponent.class));
        spriteBatchModel = new TexturePagedSpriteBatchModel(graphModels, sdfTextGraphSpriteTag,
                new SpriteBatchModelProducer() {
                    @Override
                    public SpriteBatchModel create(WritablePropertyContainer writablePropertyContainer) {
                        return new BasicSpriteBatchModel(true, 1024, graphModels, sdfTextGraphSpriteTag, writablePropertyContainer);
                    }
                });
    }

    @Override
    protected void initialize() {
        bitmapFontFunction = new Function<String, BitmapFont>() {
            @Override
            public BitmapFont apply(String s) {
                return bitmapFontSystem.getBitmapFont(s);
            }
        };
    }

    @Override
    protected void inserted(int entityId) {
        Entity textEntity = world.getEntity(entityId);
        Matrix4 resolvedTransform = transformSystem.getResolvedTransform(textEntity);

        SDFText text = new SDFText(bitmapFontFunction, spriteBatchModel, resolvedTransform,
                sdf3DTextComponentMapper.get(entityId));

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
