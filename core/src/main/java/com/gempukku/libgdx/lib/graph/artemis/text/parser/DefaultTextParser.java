package com.gempukku.libgdx.lib.graph.artemis.text.parser;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class DefaultTextParser implements TextParser {
    @Override
    public ParsedText parseText(TextStyle defaultTextStyle, String text) {
        DefaultParsedText result = Pools.obtain(DefaultParsedText.class);
        result.setTextStyle(defaultTextStyle);
        result.setText(text);
        return result;
    }

    public static class DefaultParsedText implements ParsedText, Pool.Poolable {
        private TextStyle textStyle;
        private String text;

        public void setTextStyle(TextStyle textStyle) {
            this.textStyle = textStyle;
        }

        public void setText(String text) {
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

        @Override
        public void reset() {
            textStyle = null;
            text = null;
        }

        @Override
        public void dispose() {
            Pools.free(this);
        }
    }
}
