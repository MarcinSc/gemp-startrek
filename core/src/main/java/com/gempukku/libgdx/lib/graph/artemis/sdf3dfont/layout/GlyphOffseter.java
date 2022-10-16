package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout;

import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.ParsedText;

public interface GlyphOffseter {
    GlyphOffsetText offsetText(ParsedText parsedText, float availableWidth, boolean wrap);
}
