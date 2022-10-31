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
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteBatchSystem;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.DefaultGlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.DefaultTextParser;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextParser;

public class TextSystem extends BaseEntitySystem {
    private BitmapFontSystem bitmapFontSystem;
    private SpriteBatchSystem spriteBatchSystem;
    private SpriteSystem spriteSystem;
    private TransformSystem transformSystem;
    private PipelineRendererSystem pipelineRendererSystem;
    private ComponentMapper<TextComponent> textComponentMapper;

    private final IntMap<Array<DisplayedText>> renderedTexts = new IntMap<>();

    private final GlyphOffseter glyphOffseter = new DefaultGlyphOffseter();
    private TextParser textParser;
    private String defaultSpriteBatchName;

    public TextSystem() {
        this(new DefaultTextParser());
    }

    public TextSystem(TextParser textParser) {
        super(Aspect.all(TextComponent.class));
        this.textParser = textParser;
    }

    public void setDefaultSpriteBatchName(String defaultSpriteBatchName) {
        this.defaultSpriteBatchName = defaultSpriteBatchName;
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

        Array<DisplayedText> texts = new Array<>();
        TextComponent textComponent = textComponentMapper.get(entityId);
        for (TextBlock textBlock : textComponent.getTextBlocks()) {
            String spriteBatchName = textBlock.getSpriteBatchName();
            if (spriteBatchName == null)
                spriteBatchName = defaultSpriteBatchName;

            SpriteBatchModel spriteBatchModel = spriteBatchSystem.getSpriteBatchModel(spriteBatchName);

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

    @Override
    public void dispose() {
        for (Array<DisplayedText> textsArray : renderedTexts.values()) {
            for (DisplayedText displayedText : textsArray) {
                displayedText.dispose();
            }
        }
        renderedTexts.clear();
    }
}
