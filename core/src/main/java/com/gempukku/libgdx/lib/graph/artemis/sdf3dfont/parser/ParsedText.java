package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser;

/**
 * Parsed text that provides information about the unbreakable chunks, as well as
 * a style of each glyph to render.
 */
public interface ParsedText {
    /**
     * Returns length of next unbreakable run of glyphs, starting at startIndex.
     * If this method returns -1, it means that there is no more glyphs left.
     *
     * @param startIndex
     * @return
     */
    int getNextUnbreakableChunkLength(int startIndex);

    TextStyle getTextStyle(int glyphIndex);

    char getCharAt(int glyphIndex);
}
