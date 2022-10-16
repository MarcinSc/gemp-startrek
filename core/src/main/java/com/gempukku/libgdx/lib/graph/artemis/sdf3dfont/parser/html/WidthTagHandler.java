package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;

public class WidthTagHandler implements TagHandler {
    @Override
    public String startProcessingTag(String tagParameters, Array<TextStyle> textStyleStack) {
        Float width = Float.parseFloat(tagParameters.trim());

        TextStyle lastTextStyle = textStyleStack.peek();
        TextStyle duplicated = lastTextStyle.duplicate();
        duplicated.setAttribute(TextStyleConstants.FontWidth, width);

        textStyleStack.add(duplicated);

        return null;
    }

    @Override
    public void endProcessingTag() {

    }
}
