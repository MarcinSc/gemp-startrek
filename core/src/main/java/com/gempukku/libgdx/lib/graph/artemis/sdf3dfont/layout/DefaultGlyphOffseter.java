package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.ParsedText;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.parser.TextStyleConstants;

public class DefaultGlyphOffseter implements GlyphOffseter {
    private boolean defaultKerning = true;
    private float defaultLetterSpacing = 0f;
    private float defaultLineSpacing = 0f;
    private float defaultFontScale = 1f;

    public void setDefaultKerning(boolean defaultKerning) {
        this.defaultKerning = defaultKerning;
    }

    public void setDefaultLetterSpacing(float defaultLetterSpacing) {
        this.defaultLetterSpacing = defaultLetterSpacing;
    }

    public void setDefaultLineSpacing(float defaultLineSpacing) {
        this.defaultLineSpacing = defaultLineSpacing;
    }

    @Override
    public GlyphOffsetText offsetText(ParsedText parsedText, float availableWidth, boolean wrap) {
        float width = 0;
        float height = 0;

        int nextCharacterIndex = 0;

        DefaultGlyphOffsetText text = new DefaultGlyphOffsetText();

        DefaultGlyphOffsetLine lastLine = null;

        do {
            DefaultGlyphOffsetLine line = layoutLine(parsedText, availableWidth, nextCharacterIndex, wrap);
            if (line == null)
                break;

            if (lastLine != null) {
                float lastLineSpacing = getLineSpacing(lastLine.getGlyphStyle(0));
                height += lastLineSpacing;
                lastLine.lineHeight += lastLineSpacing;
            }

            text.lines.add(line);

            width = Math.max(width, line.getWidth());
            height += line.getHeight();
            nextCharacterIndex = nextCharacterIndex + line.getGlyphCount();
            lastLine = line;
        } while (wrap);

        text.textWidth = width;
        text.textHeight = height;

        return text;
    }

    private DefaultGlyphOffsetLine layoutLine(ParsedText parsedText, float availableWidth, int startIndex, boolean wrap) {
        float usedWidth = 0f;

        int lineGlyphLength = determineLineGlyphLength(parsedText, availableWidth, startIndex, wrap);
        // Check if no more lines available
        if (lineGlyphLength == 0)
            return null;

        // Calculate max ascent and descent of the line
        float maxAscent = 0f;
        float maxDescent = 0f;
        for (int i = startIndex; i < startIndex + lineGlyphLength; i++) {
            TextStyle textStyle = parsedText.getTextStyle(i);
            char character = parsedText.getCharAt(i);

            // If it's not the last character, or not whitespace - use to calculate ascent/descent
            if (i != startIndex + lineGlyphLength - 1 || !isSkippable(character)) {
                BitmapFont font = getFont(textStyle);
                float fontScale = getFontScale(textStyle);
                maxDescent = Math.max(maxDescent, getFontDescent(font) * fontScale);
                maxAscent = Math.max(maxAscent, font.getAscent() * fontScale);
            }
        }

        DefaultGlyphOffsetLine line = new DefaultGlyphOffsetLine();

        char lastCharacter = 0;
        TextStyle lastCharacterStyle = null;

        for (int i = startIndex; i < startIndex + lineGlyphLength; i++) {
            TextStyle textStyle = parsedText.getTextStyle(i);
            char character = parsedText.getCharAt(i);

            // If it's not the last character, or not whitespace - layout the char
            if (i != startIndex + lineGlyphLength - 1 || !isSkippable(character)) {
                BitmapFont font = getFont(textStyle);
                BitmapFont.BitmapFontData fontData = font.getData();
                BitmapFont.Glyph glyph = fontData.getGlyph(character);
                float fontScale = getFontScale(textStyle);

                float kerning = 0f;
                if (lastCharacter != 0 && lastCharacterStyle == textStyle && getKerning(textStyle)) {
                    kerning = fontData.getGlyph(lastCharacter).getKerning(character);
                }
                line.xAdvances.add(usedWidth + kerning * fontScale);
                line.yAdvances.add(maxAscent - font.getAscent() * fontScale);

                float glyphAdvance = (glyph.xadvance - fontData.padLeft - fontData.padRight) + kerning + getLetterSpacing(textStyle);

                usedWidth += glyphAdvance * fontScale;

                lastCharacterStyle = textStyle;
                lastCharacter = character;
            }
        }
        line.xAdvances.add(usedWidth);
        line.yAdvances.add(0f);
        line.glyphStartIndex = startIndex;
        line.glyphCount = lineGlyphLength;
        line.parsedText = parsedText;
        line.lineWidth = usedWidth;
        line.lineHeight = maxAscent + maxDescent;

        return line;
    }

    private float getFontDescent(BitmapFont font) {
        BitmapFont.BitmapFontData fontData = font.getData();
        return font.getLineHeight() - fontData.padTop - fontData.padBottom - font.getAscent();
    }

    private int determineLineGlyphLength(ParsedText parsedText, float availableWidth, int startIndex, boolean wrap) {
        float usedWidth = 0f;
        int consumedGlyphIndex = startIndex;

        boolean firstChunk = true;
        while (true) {
            int chunkLength = parsedText.getNextUnbreakableChunkLength(consumedGlyphIndex);
            // Check if there is no more chunks left
            if (chunkLength < 0)
                return consumedGlyphIndex - startIndex;
            // Add the trailing whitespace width (if any) of a previous chunk (if not first chunk)
            if (!firstChunk) {
                usedWidth += getCharacterWidthIfSkippable(parsedText, consumedGlyphIndex - 1);
            }

            float chunkWidth = getChunkWidthExcludingLastSkippable(parsedText, consumedGlyphIndex, chunkLength);
            if (wrap && !firstChunk && usedWidth + chunkWidth > availableWidth)
                return consumedGlyphIndex - startIndex;
            usedWidth += chunkWidth;

            consumedGlyphIndex += chunkLength;
            firstChunk = false;
        }
    }

    private float getCharacterWidthIfSkippable(ParsedText parsedText, int glyphIndex) {
        char character = parsedText.getCharAt(glyphIndex);
        if (isSkippable(character)) {
            TextStyle textStyle = parsedText.getTextStyle(glyphIndex);

            BitmapFont.BitmapFontData fontData = getFontData(textStyle);
            BitmapFont.Glyph glyph = fontData.getGlyph(character);

            float fontScale = getFontScale(textStyle);

            return ((glyph.xadvance - fontData.padLeft - fontData.padRight) + getLetterSpacing(textStyle)) * fontScale;
        } else {
            return 0f;
        }
    }

    private float getChunkWidthExcludingLastSkippable(ParsedText parsedText, int startIndex, int length) {
        float width = 0;
        char lastCharacter = 0;
        TextStyle lastCharacterStyle = null;

        for (int i = startIndex; i < startIndex + length; i++) {
            TextStyle textStyle = parsedText.getTextStyle(i);
            char character = parsedText.getCharAt(i);

            // If it's not the last character, or not whitespace - add the width
            if (i != startIndex + length - 1 || !isSkippable(character)) {
                BitmapFont.BitmapFontData fontData = getFontData(textStyle);
                BitmapFont.Glyph glyph = fontData.getGlyph(character);

                float fontScale = getFontScale(textStyle);

                float glyphAdvance = (glyph.xadvance - fontData.padLeft - fontData.padRight);
                if (lastCharacter != 0 && lastCharacterStyle == textStyle && getKerning(textStyle)) {
                    int kerning = fontData.getGlyph(lastCharacter).getKerning(character);
                    glyphAdvance += kerning;
                }

                width += (glyphAdvance + getLetterSpacing(textStyle)) * fontScale;

                lastCharacterStyle = textStyle;
                lastCharacter = character;
            }
        }
        return width;
    }

    private boolean isSkippable(char character) {
        return Character.isWhitespace(character);
    }

    private BitmapFont.BitmapFontData getFontData(TextStyle textStyle) {
        return getFont(textStyle).getData();
    }

    private BitmapFont getFont(TextStyle textStyle) {
        return (BitmapFont) textStyle.getAttribute(TextStyleConstants.Font);
    }

    private Boolean getKerning(TextStyle textStyle) {
        Boolean kerning = (Boolean) textStyle.getAttribute(TextStyleConstants.Kerning);
        return kerning != null ? kerning : defaultKerning;
    }

    private float getLetterSpacing(TextStyle textStyle) {
        Float letterSpacing = (Float) textStyle.getAttribute(TextStyleConstants.LetterSpacing);
        return letterSpacing != null ? letterSpacing : defaultLetterSpacing;
    }

    private float getLineSpacing(TextStyle textStyle) {
        Float lineSpacing = (Float) textStyle.getAttribute(TextStyleConstants.LineSpacing);
        return lineSpacing != null ? lineSpacing : defaultLineSpacing;
    }

    private float getFontScale(TextStyle textStyle) {
        Float fontScale = (Float) textStyle.getAttribute(TextStyleConstants.FontScale);
        return fontScale != null ? fontScale : defaultFontScale;
    }

    private static class DefaultGlyphOffsetText implements GlyphOffsetText {
        private float textWidth;
        private float textHeight;
        private Array<DefaultGlyphOffsetLine> lines = new Array<>();

        @Override
        public float getTextWidth() {
            return textWidth;
        }

        @Override
        public float getTextHeight() {
            return textHeight;
        }

        @Override
        public int getLineCount() {
            return lines.size;
        }

        @Override
        public GlyphOffsetLine getLine(int index) {
            return lines.get(index);
        }

        @Override
        public TextStyle getLineStyle(int index) {
            return getLine(index).getGlyphStyle(0);
        }
    }

    private static class DefaultGlyphOffsetLine implements GlyphOffsetLine {
        private ParsedText parsedText;
        private float lineWidth;
        private float lineHeight;
        private int glyphStartIndex;
        private int glyphCount;
        private FloatArray xAdvances = new FloatArray();
        private FloatArray yAdvances = new FloatArray();

        @Override
        public float getWidth() {
            return lineWidth;
        }

        @Override
        public float getHeight() {
            return lineHeight;
        }

        @Override
        public int getGlyphCount() {
            return glyphCount;
        }

        @Override
        public float getGlyphXAdvance(int glyphIndex) {
            return xAdvances.get(glyphIndex);
        }

        @Override
        public float getGlyphYAdvance(int glyphIndex) {
            return yAdvances.get(glyphIndex);
        }

        @Override
        public TextStyle getGlyphStyle(int glyphIndex) {
            return parsedText.getTextStyle(glyphStartIndex + glyphIndex);
        }

        @Override
        public char getGlyph(int glyphIndex) {
            return parsedText.getCharAt(glyphStartIndex + glyphIndex);
        }
    }
}
