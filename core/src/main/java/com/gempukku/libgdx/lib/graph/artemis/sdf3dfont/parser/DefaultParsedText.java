package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser;

public class DefaultParsedText implements ParsedText {
    private TextStyle textStyle;
    private String text;

    public DefaultParsedText(TextStyle textStyle, String text) {
        this.textStyle = textStyle;
        this.text = text;
    }

    @Override
    public int getNextUnbreakableChunkLength(int startIndex) {
        if (startIndex >= text.length())
            return -1;
        int textLength = text.length();
        for (int i = startIndex; i < textLength; i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c))
                return 1 + i - startIndex;
        }
        return textLength - startIndex;
    }

    @Override
    public TextStyle getTextStyle(int glyphIndex) {
        return textStyle;
    }

    @Override
    public char getCharAt(int glyphIndex) {
        return text.charAt(glyphIndex);
    }
}
