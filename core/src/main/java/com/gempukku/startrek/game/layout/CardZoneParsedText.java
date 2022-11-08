package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.ParsedText;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;

public class CardZoneParsedText implements ParsedText {
    private static final Object spaceGlyph = new Object();
    private static final Object lineBreakGlyph = new Object();
    private final Array<Object> glyphs = new Array<>();
    private final Array<TextStyle> textStyles = new Array<>();

    private final CardZoneCards cardZoneCards;
    private float lineHeight;
    private float stackWidth;
    private float stackHorizontalGap;
    private float stackStickoutPerc;

    public CardZoneParsedText(CardZoneCards cardZoneCards, float lineHeight, float stackWidth,
                              float stackHorizontalGap, float stackStickoutPerc) {
        this.cardZoneCards = cardZoneCards;
        this.lineHeight = lineHeight;
        this.stackWidth = stackWidth;
        this.stackHorizontalGap = stackHorizontalGap;
        this.stackStickoutPerc = stackStickoutPerc;

        int lineCount = cardZoneCards.getLineCount();
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            Array<Entity> cardsInLine = cardZoneCards.getTopLevelCards(lineIndex);
            addCards(lineIndex, cardsInLine);
        }
    }

    private void addCards(int lineIndex, Array<Entity> topLevelCards) {
        boolean first = true;
        for (int i = 0; i < topLevelCards.size; i++) {
            Entity entity = topLevelCards.get(i);
            TextStyle textStyle = cardZoneCards.getCardTextStyle(lineIndex, i);
            if (!first) {
                addGlyphs(spaceGlyph, textStyle);
            }
            addGlyphs(entity, textStyle);
            first = false;
        }
        if (topLevelCards.size > 0) {
            addGlyphs(lineBreakGlyph, null);
        }
    }

    private void addGlyphs(Object glyph, TextStyle textStyle) {
        glyphs.add(glyph);
        textStyles.add(textStyle);
    }

    @Override
    public int getNextUnbreakableChunkLength(int startIndex) {
        int textLength = glyphs.size;
        if (startIndex >= textLength)
            return -1;
        for (int i = startIndex; i < textLength; i++) {
            Object glyph = glyphs.get(i);
            if (!(glyph instanceof Entity))
                return 1 + i - startIndex;
        }
        return textLength - startIndex;
    }

    @Override
    public TextStyle getTextStyle(int glyphIndex) {
        return textStyles.get(glyphIndex);
    }

    @Override
    public float getKerning(int glyphIndex) {
        return 0;
    }

    @Override
    public float getDescent(TextStyle style) {
        return 0;
    }

    @Override
    public float getAscent(TextStyle style) {
        return lineHeight;
    }

    @Override
    public float getWidth(int glyphIndex) {
        Object glyph = glyphs.get(glyphIndex);
        if (glyph == spaceGlyph)
            return stackHorizontalGap;
        return getStackWidth((Entity) glyph);
    }

    private float getStackWidth(Entity topLevelCard) {
        return stackWidth + cardZoneCards.getAttachedCardCount(topLevelCard) * (stackWidth * stackStickoutPerc);
    }

    @Override
    public boolean isWhitespace(int glyphIndex) {
        return !(glyphs.get(glyphIndex) instanceof Entity);
    }

    @Override
    public boolean isLineBreak(int glyphIndex) {
        return glyphs.get(glyphIndex) == lineBreakGlyph;
    }

    public Entity getEntity(int glyphIndex) {
        return (Entity) glyphs.get(glyphIndex);
    }

    @Override
    public void dispose() {

    }
}
