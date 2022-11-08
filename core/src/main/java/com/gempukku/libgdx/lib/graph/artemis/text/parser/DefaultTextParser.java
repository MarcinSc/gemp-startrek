package com.gempukku.libgdx.lib.graph.artemis.text.parser;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.gempukku.libgdx.lib.graph.artemis.text.FontUtil;

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
        public float getDescent(TextStyle style) {
            BitmapFont font = getFont(style);
            return FontUtil.getFontDescent(font);
        }

        @Override
        public float getAscent(TextStyle style) {
            BitmapFont font = getFont(style);
            return FontUtil.getFontAscent(font);
        }

        @Override
        public char getCharAt(int glyphIndex) {
            return text.charAt(glyphIndex);
        }

        @Override
        public boolean isSkippable(int glyphIndex) {
            return Character.isWhitespace(getCharAt(glyphIndex));
        }

        private BitmapFont getFont(TextStyle textStyle) {
            return (BitmapFont) textStyle.getAttribute(TextStyleConstants.Font);
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
