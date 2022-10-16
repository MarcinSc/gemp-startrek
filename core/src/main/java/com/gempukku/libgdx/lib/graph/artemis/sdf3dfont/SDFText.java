package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
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
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout.GlyphOffsetLine;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout.GlyphOffsetText;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout.GlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.ParsedText;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextParser;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.util.Alignment;

public class SDFText implements Disposable {
    private static Matrix4 tempMatrix = new Matrix4();
    private static Vector3 tempVector1 = new Vector3();
    private static Vector3 tempVector2 = new Vector3();
    private static Vector3 tempVector3 = new Vector3();

    private GlyphOffseter glyphOffseter;
    private TextParser textParser;
    private SpriteBatchModel spriteBatchModel;
    private BitmapFontSystem bitmapFontSystem;
    private Matrix4 transform;
    private SDF3DTextComponent sdf3DTextComponent;

    private ObjectSet<RenderableSprite> tagGraphSprites = new ObjectSet<>();

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

    public SDFText(GlyphOffseter glyphOffseter, TextParser textParser, SpriteBatchModel spriteBatchModel,
                   BitmapFontSystem bitmapFontSystem,
                   Matrix4 transform, SDF3DTextComponent sdf3DTextComponent) {
        this.glyphOffseter = glyphOffseter;
        this.textParser = textParser;
        this.spriteBatchModel = spriteBatchModel;
        this.bitmapFontSystem = bitmapFontSystem;
        this.transform = transform;
        this.sdf3DTextComponent = sdf3DTextComponent;
        addText();
    }

    public void updateSprites() {
        removeText();
        addText();
    }

    private void addText() {
        float width = sdf3DTextComponent.getRightVector().len();
        float height = sdf3DTextComponent.getUpVector().len();

        TextStyle sdfTextStyle = createDefaultTextStyle();

        ParsedText parsedText = textParser.parseText(sdfTextStyle, sdf3DTextComponent.getText());

        Vector3 normalizedRightVector = tempVector1.set(sdf3DTextComponent.getRightVector()).nor();
        Vector3 normalizedUpVector = tempVector2.set(sdf3DTextComponent.getUpVector()).nor();

        GlyphOffsetText offsetText;
        float scale;
        if (sdf3DTextComponent.isScaleDownToFit()) {
            offsetText = layoutTextToFit(width, height, glyphOffseter, parsedText, sdf3DTextComponent.getTargetWidth(),
                    sdf3DTextComponent.getScaleDownMultiplier(), sdf3DTextComponent.isWrap());
            scale = calculateScale(offsetText, width, height);
        } else {
            offsetText = glyphOffseter.offsetText(parsedText, sdf3DTextComponent.getTargetWidth(), sdf3DTextComponent.isWrap());
            scale = width / sdf3DTextComponent.getTargetWidth();
        }

        ObjectMap<TextStyle, PropertyContainer> stylePropertyContainerMap = new ObjectMap<>();

        Alignment alignment = sdf3DTextComponent.getAlignment();

        Matrix4 resultTransform = tempMatrix.set(transform).mul(sdf3DTextComponent.getTransform());

        float startY = alignment.applyY(offsetText.getTextHeight() * scale, height) - height / 2;

        float lineY = 0;
        for (int lineIndex = 0; lineIndex < offsetText.getLineCount(); lineIndex++) {
            GlyphOffsetLine line = offsetText.getLine(lineIndex);
            TextStyle lineStyle = offsetText.getLineStyle(lineIndex);
            Alignment horizontalAlignment = getHorizontalAlignment(lineStyle);
            float startX = horizontalAlignment.applyX(line.getWidth() * scale, width) - width / 2;
            for (int glyphIndex = 0; glyphIndex < line.getGlyphCount(); glyphIndex++) {
                char character = line.getGlyph(glyphIndex);
                TextStyle textStyle = line.getGlyphStyle(glyphIndex);
                BitmapFont bitmapFont = (BitmapFont) textStyle.getAttribute(TextStyleConstants.Font);
                BitmapFont.Glyph glyph = bitmapFont.getData().getGlyph(character);
                float charX = line.getGlyphXAdvance(glyphIndex);
                float charY = lineY + line.getGlyphYAdvance(glyphIndex);

                PropertyContainer stylePropertyContainer = getStylePropertyContainer(stylePropertyContainerMap, textStyle);

                addGlyph(glyph, bitmapFont,
                        resultTransform,
                        normalizedRightVector, normalizedUpVector, stylePropertyContainer,
                        1f,
                        startX, startY,
                        charX, charY, scale);
            }
            lineY += line.getHeight();
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

    private Object getFontWidth(TextStyle textStyle) {
        Float fontWidth = (Float) textStyle.getAttribute(TextStyleConstants.FontWidth);
        return fontWidth != null ? fontWidth : sdf3DTextComponent.getWidth();
    }

    private Object getFontEdge(TextStyle textStyle) {
        Float fontEdge = (Float) textStyle.getAttribute(TextStyleConstants.FontEdge);
        return fontEdge != null ? fontEdge : sdf3DTextComponent.getEdge();
    }

    private Color getFontColor(TextStyle textStyle) {
        Color fontColor = (Color) textStyle.getAttribute(TextStyleConstants.FontEdge);
        return fontColor != null ? fontColor : sdf3DTextComponent.getColor();
    }

    private Alignment getHorizontalAlignment(TextStyle textStyle) {
        Alignment alignment = (Alignment) textStyle.getAttribute(TextStyleConstants.AlignmentHorizontal);
        return alignment != null ? alignment : sdf3DTextComponent.getAlignment();
    }

    private TextStyle createDefaultTextStyle() {
        TextStyle sdfTextStyle = new TextStyle();
        sdfTextStyle.setAttribute(TextStyleConstants.Kerning, sdf3DTextComponent.getKerning());
        sdfTextStyle.setAttribute(TextStyleConstants.Font, bitmapFontSystem.getBitmapFont(sdf3DTextComponent.getBitmapFontPath()));
        sdfTextStyle.setAttribute(TextStyleConstants.LetterSpacing, sdf3DTextComponent.getLetterSpacing());
        return sdfTextStyle;
    }

    private GlyphOffsetText layoutTextToFit(float width, float height, GlyphOffseter glyphOffseter,
                                            ParsedText text, float targetWidth, float scaleDownMultiplier, boolean wrap) {
        float scale = width / targetWidth;
        do {
            GlyphOffsetText offsetText = glyphOffseter.offsetText(text, targetWidth, wrap);
            if (!sdf3DTextComponent.isScaleDownToFit())
                return offsetText;

            if (offsetText.getTextWidth() * scale <= width && offsetText.getTextHeight() * scale <= height)
                return offsetText;

            scale /= scaleDownMultiplier;
        } while (true);
    }

    private float calculateScale(GlyphOffsetText offsetText, float width, float height) {
        return Math.min(width / offsetText.getTextWidth(), height / offsetText.getTextHeight());
    }

    private void addGlyph(BitmapFont.Glyph glyph, BitmapFont bitmapFont,
                          Matrix4 resultTransform,
                          Vector3 normalizedRightVector, Vector3 normalizedUpVector,
                          PropertyContainer basePropertyContainer,
                          float glyphScale,
                          float startX, float startY,
                          float x, float y, float scale) {
        x += glyph.xoffset * glyphScale;
        y -= glyph.yoffset * glyphScale;

        float width = glyph.width * glyphScale;
        float height = glyph.height * glyphScale;

        float[] positionFloatArray = createPositionFloatArray(resultTransform, normalizedRightVector, normalizedUpVector, startX, startY, x, y, scale, width, height);

        TextureRegion fontTexture = bitmapFont.getRegion(glyph.page);

        float[] uvFloatArray = createUVFloatArray(glyph);

        Vector3 position = tempVector3
                .mulAdd(normalizedRightVector, startX + (x + width / 2) * scale)
                .mulAdd(normalizedUpVector, startY + (y - height / 2) * scale)
                .mul(resultTransform);

        HierarchicalPropertyContainer spriteContainer = new HierarchicalPropertyContainer(basePropertyContainer);
        spriteContainer.setValue("Position", new Vector3ValuePerVertex(positionFloatArray));
        spriteContainer.setValue("UV", new Vector2ValuePerVertex(uvFloatArray));
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

    private float[] createUVFloatArray(BitmapFont.Glyph glyph) {
        return new float[]{
                glyph.u, glyph.v2, glyph.u2, glyph.v2, glyph.u, glyph.v, glyph.u2, glyph.v};
    }

    private void removeText() {
        for (RenderableSprite tagGraphSprite : tagGraphSprites) {
            spriteBatchModel.removeSprite(tagGraphSprite);
        }
        tagGraphSprites.clear();
    }

    @Override
    public void dispose() {
        removeText();
    }
}
