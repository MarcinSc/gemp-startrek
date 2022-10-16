package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;

public class PopStyleTagHandler implements TagHandler {
    @Override
    public String startProcessingTag(String tagParameters, Array<TextStyle> textStyleStack) {
        textStyleStack.pop();
        return null;
    }

    @Override
    public void endProcessingTag() {

    }
}
