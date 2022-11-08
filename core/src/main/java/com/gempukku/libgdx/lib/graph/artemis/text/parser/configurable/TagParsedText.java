package com.gempukku.libgdx.lib.graph.artemis.text.parser.configurable;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.gempukku.libgdx.lib.graph.artemis.text.FontUtil;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.ParsedText;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyleConstants;

public class TagParsedText implements ParsedText, Pool.Poolable {
    private IntArray textStyleStarts = new IntArray();
    private Array<TextStyle> textStyleArray = new Array<>();
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public void addStyleIndex(int index, TextStyle style) {
        textStyleStarts.add(index);
        textStyleArray.add(style);
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
        for (int i = 1; i < textStyleStarts.size; i++) {
            if (textStyleStarts.get(i) > glyphIndex)
                return textStyleArray.get(i - 1);
        }
        return textStyleArray.get(textStyleArray.size - 1);
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
        return Character.isWhitespace(glyphIndex);
    }

    private BitmapFont getFont(TextStyle textStyle) {
        return (BitmapFont) textStyle.getAttribute(TextStyleConstants.Font);
    }

    @Override
    public void dispose() {
        Pools.free(this);
    }

    @Override
    public void reset() {
        textStyleStarts.clear();
        textStyleArray.clear();
        text = null;
    }
}
