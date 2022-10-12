package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;

public class SDFGlyphRun implements Pool.Poolable {
    private BitmapFont bitmapFont;
    private Array<BitmapFont.Glyph> glyphs = new Array();
    /**
     * Contains glyphs.size+1 entries: First entry is X offset relative to the drawing position. Subsequent entries are the X
     * advance relative to previous glyph position. Last entry is the width of the last glyph.
     */
    private FloatArray xAdvances = new FloatArray();
    private float width;
    private float glyphScale = 1f;

    public BitmapFont getBitmapFont() {
        return bitmapFont;
    }

    public void setBitmapFont(BitmapFont bitmapFont) {
        this.bitmapFont = bitmapFont;
    }

    public Array<BitmapFont.Glyph> getGlyphs() {
        return glyphs;
    }

    public FloatArray getxAdvances() {
        return xAdvances;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getGlyphScale() {
        return glyphScale;
    }

    public void setGlyphScale(float glyphScale) {
        this.glyphScale = glyphScale;
    }

    public void reset() {
        bitmapFont = null;
        glyphs.clear();
        xAdvances.clear();
        width = 0;
        glyphScale = 1f;
    }
}
