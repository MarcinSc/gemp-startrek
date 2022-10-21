package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;

public interface TagHandler {
    void processStartTag(String tagParameters, Array<TextStyle> textStyleStack, TagParsedText tagParsedText, StringBuilder resultText);

    void processEndTag(Array<TextStyle> textStyleStack, TagParsedText tagParsedText, StringBuilder resultText);
}
