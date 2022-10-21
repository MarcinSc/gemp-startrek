package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.TextHorizontalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;

public class HorizontalAlignmentTagHandler implements TagHandler {
    @Override
    public String startProcessingTag(String tagParameters, Array<TextStyle> textStyleStack) {
        TextHorizontalAlignment horizontalAlignment = TextHorizontalAlignment.valueOf(tagParameters.trim());

        TextStyle lastStyle = textStyleStack.peek();
        TextStyle duplicate = lastStyle.duplicate();
        duplicate.setAttribute(TextStyleConstants.AlignmentHorizontal, horizontalAlignment);

        textStyleStack.add(duplicate);

        return null;
    }

    @Override
    public void endProcessingTag(Array<TextStyle> textStyleStack) {

    }
}
