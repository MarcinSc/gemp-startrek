package com.gempukku.libgdx.lib.graph.artemis.text.parser.html;

import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyleConstants;

public class LetterSpacingTagHandler extends PushPopStyleTagHandler {
    @Override
    protected void modifyTextStyle(TextStyle textStyle, String tagParameters) {
        float letterSpacing = Float.parseFloat(tagParameters.trim());
        textStyle.setAttribute(TextStyleConstants.LetterSpacing, letterSpacing);
    }
}
