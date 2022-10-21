package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout;

import com.badlogic.gdx.utils.Disposable;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;

public interface GlyphOffsetText extends Disposable {
    float getTextHeight();

    float getTextWidth();

    TextStyle getTextStyle();

    int getLineCount();

    GlyphOffsetLine getLine(int index);

    TextStyle getLineStyle(int index);
}
