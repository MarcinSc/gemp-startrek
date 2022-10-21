package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;

public class PaddingRightTagHandler extends PushPopStyleEndTagHandler {
    @Override
    protected void modifyTextStyle(TextStyle textStyle, String tagParameters) {
        float paddingRight = Float.parseFloat(tagParameters.trim());
        textStyle.setAttribute(TextStyleConstants.PaddingRight, paddingRight);
    }
}
