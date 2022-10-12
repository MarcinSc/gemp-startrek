package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.PropertyContainer;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.WritablePropertyContainer;
import com.gempukku.libgdx.graph.shader.property.MapWritablePropertyContainer;
import com.gempukku.libgdx.graph.util.ValueOperations;
import com.gempukku.libgdx.graph.util.property.HierarchicalPropertyContainer;
import com.gempukku.libgdx.graph.util.sprite.DefaultRenderableSprite;
import com.gempukku.libgdx.graph.util.sprite.RenderableSprite;
import com.gempukku.libgdx.graph.util.sprite.SpriteBatchModel;
import com.gempukku.libgdx.lib.graph.artemis.Vector2ValuePerVertex;
import com.gempukku.libgdx.lib.graph.artemis.Vector3ValuePerVertex;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout.SDFGlyphLayout;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout.SDFGlyphRun;
import com.gempukku.libgdx.util.Alignment;

import java.util.function.Function;

public class SDFText implements Disposable {
    private static Matrix4 tempMatrix = new Matrix4();
    private static Vector3 tempVector1 = new Vector3();
    private static Vector3 tempVector2 = new Vector3();
    private static Vector3 tempVector3 = new Vector3();

    private Function<String, BitmapFont> bitmapFontFunction;
    private SpriteBatchModel spriteBatchModel;
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

    public SDFText(Function<String, BitmapFont> bitmapFontFunction, SpriteBatchModel spriteBatchModel,
                   Matrix4 transform, SDF3DTextComponent sdf3DTextComponent) {
        this.bitmapFontFunction = bitmapFontFunction;
        this.spriteBatchModel = spriteBatchModel;
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

        Vector3 normalizedRightVector = tempVector1.set(sdf3DTextComponent.getRightVector()).nor();
        Vector3 normalizedUpVector = tempVector2.set(sdf3DTextComponent.getUpVector()).nor();

        SDFGlyphLayout glyphLayout = Pools.obtain(SDFGlyphLayout.class);
        Array<SDFTextLine> text = sdf3DTextComponent.getLines();

        float scale = layoutTextToFit(width, height, sdf3DTextComponent.getBitmapFontPath(), glyphLayout, text,
                sdf3DTextComponent.getTargetWidth(), sdf3DTextComponent.getScaleDownMultiplier());

        WritablePropertyContainer basePropertyContainer = new MapWritablePropertyContainer();
        basePropertyContainer.setValue("Width", sdf3DTextComponent.getWidth());
        basePropertyContainer.setValue("Edge", sdf3DTextComponent.getEdge());
        basePropertyContainer.setValue("Color", sdf3DTextComponent.getColor());

        Array<SDFGlyphRun> lines = glyphLayout.getLines();

        Alignment alignment = sdf3DTextComponent.getAlignment();

        Matrix4 resultTransform = tempMatrix.set(transform).mul(sdf3DTextComponent.getTransform());

        float startY = alignment.applyY(glyphLayout.getHeight() * scale, height) - height / 2;

        float fontY = 0;
        for (int glyphRunIndex = 0; glyphRunIndex < lines.size; glyphRunIndex++) {
            SDFGlyphRun glyphRun = lines.get(glyphRunIndex);
            BitmapFont bitmapFont = glyphRun.getBitmapFont();
            BitmapFont.BitmapFontData data = bitmapFont.getData();
            float startX = alignment.applyX(glyphRun.getWidth() * scale, width) - width / 2;
            Array<BitmapFont.Glyph> glyphs = glyphRun.getGlyphs();
            FloatArray xAdvances = glyphRun.getxAdvances();
            for (int glyphIndex = 0; glyphIndex < glyphs.size; glyphIndex++) {
                BitmapFont.Glyph glyph = glyphs.get(glyphIndex);
                float fontX = xAdvances.get(glyphIndex);
                addGlyph(glyph, bitmapFont,
                        resultTransform,
                        normalizedRightVector, normalizedUpVector, basePropertyContainer,
                        glyphRun.getGlyphScale(),
                        startX, startY,
                        fontX, fontY, scale);
            }
            fontY += bitmapFont.getLineHeight() - data.padTop - data.padBottom;
        }

        Pools.free(glyphLayout);
    }

    private float layoutTextToFit(float width, float height, String bitmapFontPath, SDFGlyphLayout glyphLayout, Array<SDFTextLine> text,
                                  float targetWidth, float scaleDownMultiplier) {
        float scale = width / targetWidth;
        do {
            glyphLayout.layoutText(bitmapFontFunction, bitmapFontPath, text,
                    targetWidth, sdf3DTextComponent.isWrap(),
                    sdf3DTextComponent.getKerningMultiplier(), sdf3DTextComponent.getLetterSpacing());
            if (glyphLayout.getWidth() * scale <= width && glyphLayout.getHeight() * scale <= height)
                break;

            scale /= scaleDownMultiplier;
        } while (sdf3DTextComponent.isScaleDownToFit());

        return scale;
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
