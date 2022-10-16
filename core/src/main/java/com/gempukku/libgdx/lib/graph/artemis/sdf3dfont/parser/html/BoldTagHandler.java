package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;

public class BoldTagHandler implements TagHandler {
    private float fontWidth;

    public BoldTagHandler(float fontWidth) {
        this.fontWidth = fontWidth;
    }

    @Override
    public String startProcessingTag(String tagParameters, Array<TextStyle> textStyleStack) {
        TextStyle lastTextStyle = textStyleStack.peek();
        TextStyle duplicated = lastTextStyle.duplicate();
        duplicated.setAttribute(TextStyleConstants.FontWidth, fontWidth);

        textStyleStack.add(duplicated);

        return null;
    }

    @Override
    public void endProcessingTag() {

    }
}
