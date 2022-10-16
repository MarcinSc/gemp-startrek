package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;

public class NoOpTagHandler implements TagHandler {
    @Override
    public String startProcessingTag(String tagParameters, Array<TextStyle> textStyleStack) {
        return null;
    }

    @Override
    public void endProcessingTag() {

    }
}
