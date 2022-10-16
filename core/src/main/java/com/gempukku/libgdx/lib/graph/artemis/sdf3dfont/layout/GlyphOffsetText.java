package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout;

import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;

public interface GlyphOffsetText {
    float getTextHeight();

    float getTextWidth();

    int getLineCount();

    GlyphOffsetLine getLine(int index);

    TextStyle getLineStyle(int index);
}
