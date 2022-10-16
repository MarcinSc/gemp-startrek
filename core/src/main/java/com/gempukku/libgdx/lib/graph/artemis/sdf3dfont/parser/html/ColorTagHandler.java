package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;

public class ColorTagHandler implements TagHandler {
    @Override
    public String startProcessingTag(String tagParameters, Array<TextStyle> textStyleStack) {
        Color color = Color.valueOf(tagParameters.trim());

        TextStyle lastTextStyle = textStyleStack.peek();
        TextStyle duplicated = lastTextStyle.duplicate();
        duplicated.setAttribute(TextStyleConstants.FontColor, color);

        textStyleStack.add(duplicated);

        return null;
    }

    @Override
    public void endProcessingTag(Array<TextStyle> textStyleStack) {

    }
}
