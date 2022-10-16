package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout;

import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;

public interface GlyphOffsetLine {
    float getWidth();

    float getHeight();

    int getGlyphCount();

    float getGlyphXAdvance(int glyphIndex);

    float getGlyphYAdvance(int glyphIndex);

    TextStyle getGlyphStyle(int glyphIndex);

    char getGlyph(int glyphIndex);
}
