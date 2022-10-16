package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.ParsedText;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextParser;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.ConfigurableTagTextParser;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.TagHandler;

import java.util.function.Function;

public class HtmlTextParser implements TextParser {
    private ConfigurableTagTextParser tagTextParser;

    public HtmlTextParser(Function<String, BitmapFont> fontResolver, Function<String, TextureRegion> textureRegionResolver) {
        this(fontResolver, textureRegionResolver, '<', '>', '\\');
    }

    public HtmlTextParser(Function<String, BitmapFont> fontResolver, Function<String, TextureRegion> textureRegionResolver,
                          char startTagCharacter, char endTagCharacter, char escapeCharacter) {
        tagTextParser = new ConfigurableTagTextParser(fontResolver, startTagCharacter, endTagCharacter, escapeCharacter);
        // Font
        tagTextParser.addTagHandler("font", new FontTagHandler(fontResolver));
        tagTextParser.addTagHandler("/font", new PopStyleTagHandler());
        // Color
        tagTextParser.addTagHandler("color", new ColorTagHandler());
        tagTextParser.addTagHandler("/color", new PopStyleTagHandler());
        // Width
        tagTextParser.addTagHandler("width", new WidthTagHandler());
        tagTextParser.addTagHandler("/width", new PopStyleTagHandler());
        // Scale
        tagTextParser.addTagHandler("scale", new ScaleTagHandler());
        tagTextParser.addTagHandler("/scale", new PopStyleTagHandler());
        // Letter spacing
        tagTextParser.addTagHandler("letterSpacing", new LetterSpacingTagHandler());
        tagTextParser.addTagHandler("/letterSpacing", new PopStyleTagHandler());
        // Padding
        tagTextParser.addTagHandler("paddingLeft", new PaddingLeftTagHandler());
        tagTextParser.addTagHandler("/paddingLeft", new PopStyleTagHandler());
        tagTextParser.addTagHandler("paddingRight", new PaddingRightTagHandler());
        tagTextParser.addTagHandler("/paddingRight", new PopStyleTagHandler());

        // Image
        tagTextParser.addTagHandler("img", new ImageTagHandler(textureRegionResolver));
    }

    public void addTagHandler(String tag, TagHandler tagHandler) {
        tagTextParser.addTagHandler(tag, tagHandler);
    }

    @Override
    public ParsedText parseText(TextStyle defaultTextStyle, String text) {
        return tagTextParser.parseText(defaultTextStyle, text);
    }
}
