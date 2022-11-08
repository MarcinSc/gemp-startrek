package com.gempukku.libgdx.lib.graph.artemis.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pools;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.PropertyContainer;
import com.gempukku.libgdx.graph.shader.property.MapWritablePropertyContainer;
import com.gempukku.libgdx.graph.util.ValueOperations;
import com.gempukku.libgdx.graph.util.property.HierarchicalPropertyContainer;
import com.gempukku.libgdx.graph.util.sprite.DefaultRenderableSprite;
import com.gempukku.libgdx.graph.util.sprite.RenderableSprite;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.lib.artemis.font.BitmapFontSystem;
import com.gempukku.libgdx.lib.graph.artemis.Vector2ValuePerVertex;
import com.gempukku.libgdx.lib.graph.artemis.Vector3ValuePerVertex;
import com.gempukku.libgdx.lib.graph.artemis.VectorUtil;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteDefinition;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteDefinitionAdapter;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffsetLine;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffsetText;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.*;

public class DisplayedText implements Disposable {
    private static Matrix4 tempMatrix = new Matrix4();
    private static Vector3 tempVector1 = new Vector3();
    private static Vector3 tempVector2 = new Vector3();
    private static Vector3 tempVector3 = new Vector3();

    private GlyphOffseter glyphOffseter;
    private CharacterTextParser textParser;
    private SpriteBatchModel spriteBatchModel;
    private BitmapFontSystem bitmapFontSystem;
    private Matrix4 transform;
    private TextBlock textBlock;
    private SpriteSystem spriteSystem;

    private ObjectSet<RenderableSprite> tagGraphSprites = new ObjectSet<>();
    private ObjectSet<SpriteDefinitionAdapter> externalSprites = new ObjectSet<>();

    /*
Up-Vector (vec3) - vector defining both height and up direction
Right-Vector (vec3) - vector defining both width and right direction
Font-Texture (texture-region) - font texture region
U-Range - range of U in UV of given sprite
V-Range - range of V in UV of given sprite
Width - character width
Edge - character smoothing
Color - character color
     */

    public DisplayedText(GlyphOffseter glyphOffseter, CharacterTextParser textParser, SpriteBatchModel spriteBatchModel,
                         BitmapFontSystem bitmapFontSystem, SpriteSystem spriteSystem,
                         Matrix4 transform, TextBlock textBlock) {
        this.glyphOffseter = glyphOffseter;
        this.textParser = textParser;
        this.spriteBatchModel = spriteBatchModel;
        this.bitmapFontSystem = bitmapFontSystem;
        this.spriteSystem = spriteSystem;
        this.transform = transform;
        this.textBlock = textBlock;
        addText();
    }

    public void updateSprites() {
        removeText();
        addText();
    }

    private void addText() {
        TextStyle defaultTextStyle = createDefaultTextStyle();

        CharacterParsedText parsedText = textParser.parseText(defaultTextStyle, textBlock.getText());
        if (parsedText.getNextUnbreakableChunkLength(0) == -1)
            return;

        float widthInWorld = textBlock.getRightVector().len();
        float heightInWorld = textBlock.getUpVector().len();

        try {
            float targetWidth = textBlock.getTargetWidth();
            boolean wrap = textBlock.isWrap();

            GlyphOffsetText offsetText;
            float scale;
            if (textBlock.isScaleDownToFit()) {
                offsetText = layoutTextToFit(widthInWorld, heightInWorld, glyphOffseter, parsedText, textBlock.getTargetWidth(),
                        textBlock.getScaleDownMultiplier(), textBlock.isWrap());
                scale = calculateScale(offsetText, widthInWorld, heightInWorld);
            } else {
                offsetText = glyphOffseter.offsetText(parsedText, targetWidth, wrap);
                scale = widthInWorld / targetWidth;
            }

            Vector3 unitRightVector = tempVector1.set(textBlock.getRightVector()).nor().scl(scale);
            Vector3 unitUpVector = tempVector2.set(textBlock.getUpVector()).nor().scl(scale);

            float widthInGlyph = widthInWorld / scale;
            float heightInGlyph = heightInWorld / scale;

            ObjectMap<TextStyle, PropertyContainer> stylePropertyContainerMap = new ObjectMap<>();

            TextVerticalAlignment alignment = getVerticalAlignment(offsetText.getTextStyle());

            Matrix4 resultTransform = tempMatrix.set(transform).mul(textBlock.getTransform());

            final float startY = alignment.apply(offsetText.getTextHeight(), heightInGlyph) - heightInGlyph / 2;

            float lineY = 0;
            for (int lineIndex = 0; lineIndex < offsetText.getLineCount(); lineIndex++) {
                GlyphOffsetLine line = offsetText.getLine(lineIndex);
                float lineHeight = line.getHeight();

                TextStyle lineStyle = offsetText.getLineStyle(lineIndex);
                TextHorizontalAlignment horizontalAlignment = getHorizontalAlignment(lineStyle);
                final float startX = horizontalAlignment.apply(line.getWidth(), widthInGlyph) - widthInGlyph / 2;
                for (int glyphIndex = 0; glyphIndex < line.getGlyphCount(); glyphIndex++) {
                    char character = parsedText.getCharAt(line.getStartIndex() + glyphIndex);
                    TextStyle textStyle = line.getGlyphStyle(glyphIndex);
                    BitmapFont bitmapFont = (BitmapFont) textStyle.getAttribute(TextStyleConstants.Font);
                    BitmapFont.Glyph glyph = bitmapFont.getData().getGlyph(character);

                    float fontScale = getFontScale(textStyle);

                    float charX = line.getGlyphXAdvance(glyphIndex);
                    float charY = lineY + line.getGlyphYAdvance(glyphIndex);

                    TextureRegion textureRegion = getTextureRegion(textStyle);
                    if (textureRegion != null) {
                        SpriteDefinition spriteDefinition = new SpriteDefinition();
                        spriteDefinition.setSpriteBatchName(getSpriteSystemName(textStyle));
                        spriteDefinition.getProperties().put("UV", SpriteSystem.uvAttribute);
                        float textureHeight = FontUtil.getFontAscent(bitmapFont);
                        float textureWidth = textureHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();
                        Vector3ValuePerVertex positionFloatArray = VectorUtil.createSideSpritePosition(
                                startX + charX, startY + charY,
                                textureWidth, textureHeight,
                                unitRightVector, unitUpVector,
                                resultTransform);
                        spriteDefinition.getProperties().put("Position", positionFloatArray);
                        spriteDefinition.getProperties().put("Texture", textureRegion);

                        SpriteDefinitionAdapter sprite = spriteSystem.addSprite(null, null, spriteDefinition);
                        externalSprites.add(sprite);
                    } else {
                        PropertyContainer stylePropertyContainer = getStylePropertyContainer(stylePropertyContainerMap, textStyle);

                        addGlyph(glyph, bitmapFont,
                                resultTransform,
                                unitRightVector, unitUpVector, stylePropertyContainer,
                                fontScale,
                                startX, startY,
                                charX, charY);
                    }
                }
                lineY += lineHeight;
            }
        } finally {
            parsedText.dispose();
            Pools.free(defaultTextStyle);
        }
    }

    private PropertyContainer getStylePropertyContainer(ObjectMap<TextStyle, PropertyContainer> stylePropertyContainerMap, TextStyle textStyle) {
        PropertyContainer result = stylePropertyContainerMap.get(textStyle);
        if (result == null) {
            MapWritablePropertyContainer container = new MapWritablePropertyContainer();
            container.setValue("Width", getFontWidth(textStyle));
            container.setValue("Edge", getFontEdge(textStyle));
            container.setValue("Color", getFontColor(textStyle));
            result = container;

            stylePropertyContainerMap.put(textStyle, result);
        }
        return result;
    }

    private float getFontScale(TextStyle textStyle) {
        Float fontScale = (Float) textStyle.getAttribute(TextStyleConstants.GlyphScale);
        return fontScale != null ? fontScale : 1f;
    }

    private float getFontWidth(TextStyle textStyle) {
        Float fontWidth = (Float) textStyle.getAttribute(TextStyleConstants.FontWidth);
        return fontWidth != null ? fontWidth : textBlock.getWidth();
    }

    private float getFontEdge(TextStyle textStyle) {
        Float fontEdge = (Float) textStyle.getAttribute(TextStyleConstants.FontEdge);
        return fontEdge != null ? fontEdge : textBlock.getEdge();
    }

    private Color getFontColor(TextStyle textStyle) {
        Color fontColor = (Color) textStyle.getAttribute(TextStyleConstants.FontColor);
        return fontColor != null ? fontColor : textBlock.getColor();
    }

    private TextVerticalAlignment getVerticalAlignment(TextStyle textStyle) {
        TextVerticalAlignment alignment = (TextVerticalAlignment) textStyle.getAttribute(TextStyleConstants.AlignmentVertical);
        return alignment != null ? alignment : textBlock.getVerticalAlignment();
    }

    private TextHorizontalAlignment getHorizontalAlignment(TextStyle textStyle) {
        TextHorizontalAlignment alignment = (TextHorizontalAlignment) textStyle.getAttribute(TextStyleConstants.AlignmentHorizontal);
        return alignment != null ? alignment : textBlock.getHorizontalAlignment();
    }

    private TextureRegion getTextureRegion(TextStyle textStyle) {
        return (TextureRegion) textStyle.getAttribute(TextStyleConstants.ImageTextureRegion);
    }

    private String getSpriteSystemName(TextStyle textStyle) {
        return (String) textStyle.getAttribute(TextStyleConstants.ImageSpriteSystemName);
    }

    private TextStyle createDefaultTextStyle() {
        TextStyle textStyle = Pools.obtain(TextStyle.class);
        textStyle.setAttribute(TextStyleConstants.Kerning, textBlock.getKerning());
        textStyle.setAttribute(TextStyleConstants.Font, bitmapFontSystem.getBitmapFont(textBlock.getBitmapFontPath()));
        textStyle.setAttribute(TextStyleConstants.GlyphSpacing, textBlock.getLetterSpacing());
        textStyle.setAttribute(TextStyleConstants.AlignmentHorizontal, textBlock.getHorizontalAlignment());
        return textStyle;
    }

    private GlyphOffsetText layoutTextToFit(float width, float height, GlyphOffseter glyphOffseter,
                                            ParsedText text, float targetWidth, float scaleDownMultiplier, boolean wrap) {
        float scale = width / targetWidth;
        float renderScale = 1f;
        do {
            GlyphOffsetText offsetText = glyphOffseter.offsetText(text, targetWidth * renderScale, wrap);
            if (!textBlock.isScaleDownToFit())
                return offsetText;

            if (offsetText.getTextWidth() * scale <= width && offsetText.getTextHeight() * scale <= height)
                return offsetText;

            scale /= scaleDownMultiplier;
            renderScale *= scaleDownMultiplier;
        } while (true);
    }

    private float calculateScale(GlyphOffsetText offsetText, float width, float height) {
        return Math.min(width / offsetText.getTextWidth(), height / offsetText.getTextHeight());
    }

    private void addGlyph(BitmapFont.Glyph glyph, BitmapFont bitmapFont,
                          Matrix4 resultTransform,
                          Vector3 unitRightVector, Vector3 unitUpVector,
                          PropertyContainer basePropertyContainer,
                          float glyphScale,
                          float startX, float startY,
                          float glyphX, float glyphY) {
        float glyphXOffset = glyph.xoffset * glyphScale;
        float glyphYOffset = glyph.yoffset * glyphScale;

        float glyphWidth = glyph.width * glyphScale;
        float glyphHeight = glyph.height * glyphScale;

        Vector3ValuePerVertex positionFloatArray = VectorUtil.createSideSpritePosition(
                startX + glyphX + glyphXOffset, startY + glyphY - (glyphHeight + glyphYOffset),
                glyphWidth, glyphHeight,
                unitRightVector, unitUpVector,
                resultTransform);

        TextureRegion fontTexture = bitmapFont.getRegion(glyph.page);

        Vector3 position = tempVector3
                .mulAdd(unitRightVector, startX + (glyphX + glyphWidth / 2))
                .mulAdd(unitUpVector, startY + (glyphY + glyphHeight / 2))
                .mul(resultTransform);

        HierarchicalPropertyContainer spriteContainer = new HierarchicalPropertyContainer(basePropertyContainer);
        spriteContainer.setValue("Position", positionFloatArray);
        spriteContainer.setValue("UV", createUVFloatArray(glyph));
        spriteContainer.setValue("Font-Texture", fontTexture);

        DefaultRenderableSprite sprite = new DefaultRenderableSprite(spriteContainer);
        sprite.getPosition().set(position);

        spriteBatchModel.addSprite(sprite);

        tagGraphSprites.add(sprite);
    }

    private float[] createPositionFloatArray(Matrix4 resultTransform, Vector3 normalizedRightVector, Vector3 normalizedUpVector, float startX, float startY, float x, float y, float scale, float width, float height) {
        float xLeft = startX + x * scale;
        float xRight = startX + (x + width) * scale;
        float yUp = startY + (y - height) * scale;
        float yDown = startY + y * scale;

        float[] positionVector = new float[3 * 4];

        Vector3 upperLeftCorner = tempVector3.setZero()
                .mulAdd(normalizedRightVector, xLeft)
                .mulAdd(normalizedUpVector, yUp)
                .mul(resultTransform);
        ValueOperations.copyVector3IntoArray(upperLeftCorner, positionVector, 0 * 3);
        Vector3 upperRightCorner = tempVector3.setZero()
                .mulAdd(normalizedRightVector, xRight)
                .mulAdd(normalizedUpVector, yUp)
                .mul(resultTransform);
        ValueOperations.copyVector3IntoArray(upperRightCorner, positionVector, 1 * 3);
        Vector3 lowerLeftCorner = tempVector3.setZero()
                .mulAdd(normalizedRightVector, xLeft)
                .mulAdd(normalizedUpVector, yDown)
                .mul(resultTransform);
        ValueOperations.copyVector3IntoArray(lowerLeftCorner, positionVector, 2 * 3);
        Vector3 lowerRightCorner = tempVector3.setZero()
                .mulAdd(normalizedRightVector, xRight)
                .mulAdd(normalizedUpVector, yDown)
                .mul(resultTransform);
        ValueOperations.copyVector3IntoArray(lowerRightCorner, positionVector, 3 * 3);

        return positionVector;
    }

    private Vector2ValuePerVertex createUVFloatArray(BitmapFont.Glyph glyph) {
        return new Vector2ValuePerVertex(new float[]{
                glyph.u, glyph.v2, glyph.u2, glyph.v2, glyph.u, glyph.v, glyph.u2, glyph.v});
    }

    private void removeText() {
        for (RenderableSprite tagGraphSprite : tagGraphSprites) {
            spriteBatchModel.removeSprite(tagGraphSprite);
        }
        tagGraphSprites.clear();
        for (SpriteDefinitionAdapter externalSprite : externalSprites) {
            spriteSystem.removeSprite(externalSprite);
        }
        externalSprites.clear();
    }

    @Override
    public void dispose() {
        removeText();
    }
}
