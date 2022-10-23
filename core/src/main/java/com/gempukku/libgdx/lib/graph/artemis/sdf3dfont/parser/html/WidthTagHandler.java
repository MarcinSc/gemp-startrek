package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;

public class WidthTagHandler extends PushPopStyleTagHandler {
    @Override
    protected void modifyTextStyle(TextStyle textStyle, String tagParameters) {
        Float width = Float.parseFloat(tagParameters.trim());
        textStyle.setAttribute(TextStyleConstants.FontWidth, width);
    }
}
