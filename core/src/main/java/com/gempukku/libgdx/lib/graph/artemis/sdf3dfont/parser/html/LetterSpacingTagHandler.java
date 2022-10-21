package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagParsedText;

public class LetterSpacingTagHandler extends PopStyleEndTagHandler {
    @Override
    public void processStartTag(String tagParameters, Array<TextStyle> textStyleStack, TagParsedText tagParsedText, StringBuilder resultText) {
        float letterSpacing = Float.parseFloat(tagParameters.trim());

        TextStyle lastStyle = textStyleStack.peek();
        TextStyle duplicate = lastStyle.duplicate();
        duplicate.setAttribute(TextStyleConstants.LetterSpacing, letterSpacing);

        textStyleStack.add(duplicate);
    }
}
