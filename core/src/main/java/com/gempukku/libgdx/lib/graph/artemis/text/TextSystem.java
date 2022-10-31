package com.gempukku.libgdx.lib.graph.artemis.text;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.font.BitmapFontSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformUpdated;
import com.gempukku.libgdx.lib.graph.artemis.renderer.PipelineRendererSystem;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.DefaultGlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.DefaultTextParser;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextParser;

public class TextSystem extends BaseEntitySystem {
    private BitmapFontSystem bitmapFontSystem;
    private SpriteSystem spriteSystem;
    private TransformSystem transformSystem;
    private PipelineRendererSystem pipelineRendererSystem;
    private ComponentMapper<TextComponent> textComponentMapper;

    private final IntMap<Array<DisplayedText>> renderedTexts = new IntMap<>();

    private final GlyphOffseter glyphOffseter = new DefaultGlyphOffseter();
    private TextParser textParser;
    private final String spriteSystemName;

    public TextSystem(String spriteSystemName) {
        this(spriteSystemName, new DefaultTextParser());
    }

    public TextSystem(String spriteSystemName, TextParser textParser) {
        super(Aspect.all(TextComponent.class));
        this.spriteSystemName = spriteSystemName;
        this.textParser = textParser;
    }

    public void setTextParser(TextParser textParser) {
        this.textParser = textParser;
    }

    @Override
    protected void inserted(int entityId) {
        addSprites(entityId);
    }

    private void addSprites(int entityId) {
        Entity textEntity = world.getEntity(entityId);
        Matrix4 resolvedTransform = transformSystem.getResolvedTransform(textEntity);

        SpriteBatchModel spriteBatchModel = spriteSystem.getSpriteBatchModel(spriteSystemName);

        Array<DisplayedText> texts = new Array<>();
        TextComponent textComponent = textComponentMapper.get(entityId);
        for (TextBlock textBlock : textComponent.getTextBlocks()) {
            DisplayedText text = new DisplayedText(glyphOffseter, textParser, spriteBatchModel, bitmapFontSystem,
                    spriteSystem, resolvedTransform, textBlock);
            texts.add(text);
        }

        renderedTexts.put(entityId, texts);
    }

    @EventListener
    public void transformChanged(TransformUpdated transformUpdated, Entity entity) {
        Array<DisplayedText> texts = renderedTexts.get(entity.getId());
        if (texts != null) {
            for (DisplayedText text : texts) {
                text.updateSprites();
            }
        }
    }

    public void updateTexts(int entityId) {
        removed(entityId);
        addSprites(entityId);
    }

    public void updateText(int entityId, int textIndex) {
        Array<DisplayedText> texts = renderedTexts.get(entityId);
        texts.get(textIndex).updateSprites();
    }

    @Override
    protected void removed(int entityId) {
        Array<DisplayedText> texts = renderedTexts.remove(entityId);
        for (DisplayedText text : texts) {
            text.dispose();
        }
    }

    @Override
    protected void processSystem() {

    }
}
