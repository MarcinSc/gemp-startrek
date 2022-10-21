package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagParsedText;

import java.util.function.Function;

public class FontTagHandler extends PopStyleEndTagHandler {
    private Function<String, BitmapFont> fontResolver;

    public FontTagHandler(Function<String, BitmapFont> fontResolver) {
        this.fontResolver = fontResolver;
    }

    @Override
    public void processStartTag(String tagParameters, Array<TextStyle> textStyleStack, TagParsedText tagParsedText, StringBuilder resultText) {
        String fontName = tagParameters.trim();

        TextStyle lastStyle = textStyleStack.peek();
        TextStyle duplicate = lastStyle.duplicate();
        duplicate.setAttribute(TextStyleConstants.Font, fontResolver.apply(fontName));

        textStyleStack.add(duplicate);
    }
}
