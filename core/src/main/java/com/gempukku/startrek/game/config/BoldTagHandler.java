package com.gempukku.startrek.game.config;

import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.html.PushPopStyleTagHandler;

public class BoldTagHandler extends PushPopStyleTagHandler {
    @Override
    protected void modifyTextStyle(TextStyle textStyle, String tagParameters) {
        textStyle.setAttribute(TextStyleConstants.FontWidth, 0.5f);
    }
}
