package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.html;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.ParsedText;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextParser;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.configurable.ConfigurableTagTextParser;

import java.util.function.Function;

public class HtmlTextParser implements TextParser {
    private ConfigurableTagTextParser tagTextParser;

    public HtmlTextParser(Function<String, BitmapFont> fontResolver) {
        this(fontResolver, '<', '>', '\\');
    }

    public HtmlTextParser(Function<String, BitmapFont> fontResolver, char startTagCharacter, char endTagCharacter, char escapeCharacter) {
        tagTextParser = new ConfigurableTagTextParser(fontResolver, startTagCharacter, endTagCharacter, escapeCharacter);
        // Bold
        tagTextParser.addTagHandler("b", new BoldTagHandler(0.5f));
        tagTextParser.addTagHandler("/b", new PopStyleTagHandler());
        // Font
        tagTextParser.addTagHandler("font", new FontTagHandler(fontResolver));
        tagTextParser.addTagHandler("/font", new PopStyleTagHandler());
        // Color
        tagTextParser.addTagHandler("color", new ColorTagHandler());
        tagTextParser.addTagHandler("/color", new PopStyleTagHandler());
    }

    @Override
    public ParsedText parseText(TextStyle defaultTextStyle, String text) {
        return tagTextParser.parseText(defaultTextStyle, text);
    }
}
