package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;

import java.util.function.Function;

public class FontTagHandler implements TagHandler {
    private Function<String, BitmapFont> fontResolver;

    public FontTagHandler(Function<String, BitmapFont> fontResolver) {
        this.fontResolver = fontResolver;
    }

    @Override
    public String startProcessingTag(String tagParameters, Array<TextStyle> textStyleStack) {
        String fontName = tagParameters.trim();

        TextStyle lastStyle = textStyleStack.peek();
        TextStyle duplicate = lastStyle.duplicate();
        duplicate.setAttribute(TextStyleConstants.Font, fontResolver.apply(fontName));

        textStyleStack.add(duplicate);

        return null;
    }

    @Override
    public void endProcessingTag(Array<TextStyle> textStyleStack) {

    }
}
