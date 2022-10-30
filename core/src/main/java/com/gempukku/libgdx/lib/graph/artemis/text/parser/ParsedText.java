package com.gempukku.libgdx.lib.graph.artemis.text.parser;

import com.badlogic.gdx.utils.Disposable;

/**
 * Parsed text that provides information about the unbreakable chunks, as well as
 * a style of each glyph to render.
 */
public interface ParsedText extends Disposable {
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
