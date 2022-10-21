package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagParsedText;

public abstract class PopStyleEndTagHandler implements TagHandler {
    @Override
    public final void processEndTag(Array<TextStyle> textStyleStack, TagParsedText tagParsedText, StringBuilder resultText) {
        textStyleStack.pop();
    }
}
