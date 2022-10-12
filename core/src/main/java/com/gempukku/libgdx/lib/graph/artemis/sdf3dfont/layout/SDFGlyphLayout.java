package com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.layout;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.gempukku.libgdx.lib.graph.artemis.sdf3dfont.SDFTextLine;

import java.util.function.Function;

public class SDFGlyphLayout implements Pool.Poolable {
    private Array<SDFGlyphRun> lines = new Array<>();
    private float width;
    private float height;

    public void layoutText(Function<String, BitmapFont> fontMapping, String bitmapFontPath, Array<SDFTextLine> textLines,
                           float targetWidth, boolean wrap,
                           float kerningMultiplier, float letterSpacing) {
        clearLines();

        width = 0;
        height = 0;

        for (SDFTextLine line : textLines) {
            String bitmapFontOverride = line.getBitmapFontPath();

            BitmapFont font = fontMapping.apply(bitmapFontOverride != null ? bitmapFontOverride : bitmapFontPath);
            BitmapFont.BitmapFontData fontData = font.getData();

            String text = line.getText();
            float scale = line.getScale();

            float kerningMultiplierOverride = line.getKerningMultiplier();
            float resultKerningMultiplier = ((kerningMultiplierOverride != -1f) ? kerningMultiplierOverride : kerningMultiplier);

            float letterSpacingOverride = line.getLetterSpacing();
            float resultLetterSpacing = ((letterSpacingOverride != Float.MIN_VALUE) ? letterSpacingOverride : letterSpacing / scale);

            int nextCharacterIndex = 0;
            int characterCount = text.length();

            while (nextCharacterIndex < characterCount && (wrap || textLines.size == 0)) {
                SDFGlyphRun glyphRun = Pools.obtain(SDFGlyphRun.class);
                glyphRun.setBitmapFont(font);
                glyphRun.setGlyphScale(scale);
                nextCharacterIndex = layoutLine(glyphRun, fontData, text, nextCharacterIndex, targetWidth, wrap,
                        resultKerningMultiplier, resultLetterSpacing, scale);
                lines.add(glyphRun);

                width = Math.max(width, glyphRun.getWidth());
                height += (font.getLineHeight() - fontData.padTop - fontData.padBottom) * scale;
            }
        }
    }

    private int layoutLine(SDFGlyphRun glyphRun, BitmapFont.BitmapFontData fontData, CharSequence str, int index, float targetWidth,
                           boolean wrap, float kerningMultiplier, float letterSpacing, float scale) {
        int consumedIndex = index;

        FloatArray xAdvances = glyphRun.getxAdvances();
        Array<BitmapFont.Glyph> glyphs = glyphRun.getGlyphs();
        float width = 0;

        boolean firstBreak = true;
        boolean breakOnBreakChar;
        boolean lastBreakOnWhitespace = false;
        boolean breakOnWhitespace;

        char lastAppendedChar = 0;
        while (consumedIndex < str.length()) {
            int nextBreak = getNextBreak(fontData, str, consumedIndex + (lastBreakOnWhitespace ? 1 : 0));

            breakOnWhitespace = (nextBreak < str.length() && fontData.isWhitespace(str.charAt(nextBreak)));
            breakOnBreakChar = (nextBreak < str.length() && fontData.isBreakChar(str.charAt(nextBreak)));

            float nextBreakWidth = calculateNextBreakWidth(fontData, str, consumedIndex, nextBreak, breakOnBreakChar, lastAppendedChar,
                    kerningMultiplier, letterSpacing);
            if ((width + nextBreakWidth) * scale > targetWidth && !firstBreak && wrap) {
                // Need to subtract letter spacing from last letter in the line
                width -= letterSpacing;

                xAdvances.add(width * scale);
                glyphRun.setWidth(width * scale);
                if (lastBreakOnWhitespace)
                    return consumedIndex + 1;
                return consumedIndex;
            }

            for (int i = consumedIndex; i < nextBreak; i++) {
                char c = str.charAt(i);
                BitmapFont.Glyph glyph = fontData.getGlyph(c);
                if (glyph != null) {
                    if (lastAppendedChar != 0)
                        width += kerningMultiplier * fontData.getGlyph(lastAppendedChar).getKerning(c);
                    xAdvances.add(width * scale);
                    glyphs.add(glyph);
                    width += getXAdvance(fontData, c, letterSpacing);
                    lastAppendedChar = c;
                }
            }
            if (breakOnBreakChar)
                consumedIndex = nextBreak + 1;
            else
                consumedIndex = nextBreak;

            firstBreak = false;
            lastBreakOnWhitespace = breakOnWhitespace;
        }
        // Need to subtract letter spacing from last letter in the line
        width -= letterSpacing;

        xAdvances.add(width * scale);
        glyphRun.setWidth(width * scale);

        return consumedIndex;
    }

    private float calculateNextBreakWidth(BitmapFont.BitmapFontData fontData, CharSequence str, int consumedIndex, int nextBreak, boolean breakOnBreakChar, char lastAppendedChar,
                                          float kerningMultiplier, float letterSpacing) {
        char lastPlannedChar = lastAppendedChar;
        float nextBreakWidth = 0;
        for (int i = consumedIndex; i < nextBreak; i++) {
            char c = str.charAt(i);
            nextBreakWidth += getXAdvanceWithKerning(fontData, c, lastPlannedChar, kerningMultiplier, letterSpacing);
            lastPlannedChar = c;
        }
        if (breakOnBreakChar) {
            char c = str.charAt(nextBreak);
            nextBreakWidth += getXAdvanceWithKerning(fontData, c, lastPlannedChar, kerningMultiplier, letterSpacing);
        }
        // Need to subtract spacing from last letter
        return nextBreakWidth - letterSpacing;
    }

    private float getXAdvance(BitmapFont.BitmapFontData fontData, char c, float letterSpacing) {
        BitmapFont.Glyph glyph = fontData.getGlyph(c);
        if (glyph == null)
            return 0;
        return (glyph.xadvance - fontData.padLeft - fontData.padRight) + letterSpacing;
    }

    private float getXAdvanceWithKerning(BitmapFont.BitmapFontData fontData, char c, char lastChar,
                                         float kerningMultiplier, float letterSpacing) {
        BitmapFont.Glyph glyph = fontData.getGlyph(c);
        if (glyph == null)
            return 0;
        float result = (glyph.xadvance - fontData.padLeft - fontData.padRight) + letterSpacing;
        if (lastChar != 0) {
            int kerning = fontData.getGlyph(lastChar).getKerning(c);
            result += kerningMultiplier * kerning;
        }
        return result;
    }

    private int getNextBreak(BitmapFont.BitmapFontData data, CharSequence str, int start) {
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (data.isWhitespace(c) || data.isBreakChar(c))
                return i;
        }
        return str.length();
    }

    public Array<SDFGlyphRun> getLines() {
        return lines;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private void clearLines() {
        Pools.get(SDFGlyphRun.class).freeAll(lines);
        lines.clear();
    }

    @Override
    public void reset() {
        clearLines();

        width = 0;
        height = 0;
    }
}
