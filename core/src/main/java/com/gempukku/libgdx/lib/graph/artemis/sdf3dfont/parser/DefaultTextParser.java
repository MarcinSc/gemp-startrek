package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser;

public class DefaultTextParser implements TextParser {
    @Override
    public ParsedText parseText(TextStyle defaultTextStyle, String text) {
        return new DefaultParsedText(defaultTextStyle, text);
    }
}
