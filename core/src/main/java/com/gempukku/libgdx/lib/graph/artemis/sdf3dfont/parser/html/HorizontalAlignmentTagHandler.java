package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.TextHorizontalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;

public class HorizontalAlignmentTagHandler extends PushPopStyleTagHandler {
    @Override
    protected void modifyTextStyle(TextStyle textStyle, String tagParameters) {
        TextHorizontalAlignment horizontalAlignment = TextHorizontalAlignment.valueOf(tagParameters.trim());
        textStyle.setAttribute(TextStyleConstants.AlignmentHorizontal, horizontalAlignment);
    }
}
