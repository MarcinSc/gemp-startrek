package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;

public class PaddingLeftTagHandler extends PushPopStyleTagHandler {
    @Override
    protected void modifyTextStyle(TextStyle textStyle, String tagParameters) {
        float paddingLeft = Float.parseFloat(tagParameters.trim());
        textStyle.setAttribute(TextStyleConstants.PaddingLeft, paddingLeft);
    }
}
