package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;

public interface TagHandler {
    String startProcessingTag(String tagParameters, Array<TextStyle> textStyleStack);

    void endProcessingTag();
}
