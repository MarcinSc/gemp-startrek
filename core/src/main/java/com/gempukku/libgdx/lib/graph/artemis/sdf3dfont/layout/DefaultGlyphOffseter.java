package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.FontUtil;
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

    public void setDefaultFontScale(float defaultFontScale) {
        this.defaultFontScale = defaultFontScale;
    }

    @Override
    public GlyphOffsetText offsetText(ParsedText parsedText, float availableWidth, boolean wrap) {
        float width = 0;
        float height = 0;

        int nextCharacterIndex = 0;

        DefaultGlyphOffsetText text = Pools.obtain(DefaultGlyphOffsetText.class);

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
                maxDescent = Math.max(maxDescent, FontUtil.getFontDescent(font) * fontScale);
                maxAscent = Math.max(maxAscent, FontUtil.getFontAscent(font) * fontScale);
            }
        }

        DefaultGlyphOffsetLine line = Pools.obtain(DefaultGlyphOffsetLine.class);

        TextStyle lineStyle = parsedText.getTextStyle(startIndex);

        float usedWidth = getLinePaddingLeft(lineStyle);

        char lastCharacter = 0;
        TextStyle lastCharacterStyle = null;

        for (int i = startIndex; i < startIndex + lineGlyphLength; i++) {
            TextStyle textStyle = parsedText.getTextStyle(i);
            char character = parsedText.getCharAt(i);

            // If it's not the last character, or not whitespace - layout the char
            if (i != startIndex + lineGlyphLength - 1 || !isSkippable(character)) {
                BitmapFont font = getFont(textStyle);
                BitmapFont.BitmapFontData fontData = font.getData();
                float fontScale = getFontScale(textStyle);

                TextureRegion textureRegion = getTextureRegion(textStyle);
                float ascent = FontUtil.getFontAscent(font) * fontScale;
                if (textureRegion != null) {
                    line.xAdvances.add(usedWidth);
                    line.yAdvances.add(maxAscent - ascent);

                    float textureHeight = ascent;
                    float textureWidth = textureHeight * textureRegion.getRegionWidth() / textureRegion.getRegionHeight();

                    usedWidth += textureWidth + getLetterSpacing(textStyle) * fontScale;
                } else {
                    BitmapFont.Glyph glyph = fontData.getGlyph(character);

                    float kerning = 0f;
                    if (lastCharacter != 0 && lastCharacterStyle == textStyle && getKerning(textStyle)) {
                        kerning = fontData.getGlyph(lastCharacter).getKerning(character);
                    }
                    line.xAdvances.add(usedWidth + kerning * fontScale);
                    line.yAdvances.add(maxAscent - ascent);

                    float glyphAdvance = glyph.xadvance + kerning + getLetterSpacing(textStyle);

                    usedWidth += glyphAdvance * fontScale;
                }

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
            } else {
                TextStyle lineStyle = parsedText.getTextStyle(startIndex);
                usedWidth += getLinePaddingLeft(lineStyle);
                availableWidth -= getLinePaddingRight(lineStyle);
            }

            float chunkWidth = getChunkWidthExcludingLastSkippable(parsedText, consumedGlyphIndex, chunkLength);
            if (wrap && !firstChunk && usedWidth + chunkWidth > availableWidth)
                return consumedGlyphIndex - startIndex;
            usedWidth += chunkWidth;

            consumedGlyphIndex += chunkLength;

            if (parsedText.getCharAt(consumedGlyphIndex - 1) == '\n')
                return consumedGlyphIndex - startIndex;

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

            return (glyph.xadvance + getLetterSpacing(textStyle)) * fontScale;
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
                BitmapFont font = getFont(textStyle);
                BitmapFont.BitmapFontData fontData = font.getData();
                BitmapFont.Glyph glyph = fontData.getGlyph(character);

                float fontScale = getFontScale(textStyle);

                TextureRegion textureRegion = getTextureRegion(textStyle);
                float ascent = FontUtil.getFontAscent(font) * fontScale;
                if (textureRegion != null) {
                    width += 5 * (textureRegion.getRegionWidth() * ascent / textureRegion.getRegionHeight())
                            + getLetterSpacing(textStyle) * fontScale;
                } else {
                    float glyphAdvance = glyph.xadvance;
                    if (lastCharacter != 0 && lastCharacterStyle == textStyle && getKerning(textStyle)) {
                        int kerning = fontData.getGlyph(lastCharacter).getKerning(character);
                        glyphAdvance += kerning;
                    }

                    width += (glyphAdvance + getLetterSpacing(textStyle)) * fontScale;
                }

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

    private TextureRegion getTextureRegion(TextStyle textStyle) {
        return (TextureRegion) textStyle.getAttribute(TextStyleConstants.ImageTextureRegion);
    }

    private float getLinePaddingLeft(TextStyle lineStyle) {
        Float linePaddingLeft = (Float) lineStyle.getAttribute(TextStyleConstants.PaddingLeft);
        return linePaddingLeft != null ? linePaddingLeft : 0f;
    }

    private float getLinePaddingRight(TextStyle lineStyle) {
        Float linePaddingRight = (Float) lineStyle.getAttribute(TextStyleConstants.PaddingRight);
        return linePaddingRight != null ? linePaddingRight : 0f;
    }

    public static class DefaultGlyphOffsetText implements GlyphOffsetText, Pool.Poolable {
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
        public TextStyle getTextStyle() {
            return getLineStyle(0);
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

        @Override
        public void dispose() {
            Pools.free(this);
        }

        @Override
        public void reset() {
            textWidth = 0f;
            textHeight = 0;
            Pools.freeAll(lines);
            lines.clear();
        }
    }

    private static class DefaultGlyphOffsetLine implements GlyphOffsetLine, Pool.Poolable {
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

        @Override
        public void reset() {
            parsedText = null;
            lineWidth = 0f;
            lineHeight = 0f;
            glyphStartIndex = 0;
            glyphCount = 0;
            xAdvances.clear();
            yAdvances.clear();
        }
    }
}
