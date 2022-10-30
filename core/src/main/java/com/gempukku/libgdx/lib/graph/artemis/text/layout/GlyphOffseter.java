package com.gempukku.libgdx.lib.graph.artemis.text.layout;

import com.gempukku.libgdx.lib.graph.artemis.text.parser.ParsedText;

public interface GlyphOffseter {
    GlyphOffsetText offsetText(ParsedText parsedText, float availableWidth, boolean wrap);
}
