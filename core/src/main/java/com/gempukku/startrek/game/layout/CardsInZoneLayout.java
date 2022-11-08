package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.TextHorizontalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.text.TextVerticalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffsetLine;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffsetText;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyleConstants;

public class CardsInZoneLayout {
    private static final Matrix4 tempMatrix4 = new Matrix4();

    public static void layoutCards(TransformSystem transformSystem, GlyphOffseter glyphOffseter,
                                   Matrix4 startupTransform, CardZoneParsedText missionParsedText,
                                   float maximumScale, float availableWidth, float availableHeight) {
        GlyphOffsetText missionLayout = layoutMissionLines(glyphOffseter, missionParsedText,
                maximumScale, availableWidth, availableHeight);
        float scale = Math.min(maximumScale, calculateScale(missionLayout, availableWidth, availableHeight));

        // Scale the cards
        startupTransform.scale(scale, 1f, scale);

        float startY = TextVerticalAlignment.center.apply(missionLayout.getTextHeight(), availableHeight) - availableHeight / 2f;
        for (int lineIndex = 0; lineIndex < missionLayout.getLineCount(); lineIndex++) {
            GlyphOffsetLine line = missionLayout.getLine(lineIndex);
            float lineWidth = line.getWidth();
            float startX = TextHorizontalAlignment.center.apply(lineWidth, availableWidth) - availableWidth / 2f;
            for (int glyphIndex = 0; glyphIndex < line.getGlyphCount(); glyphIndex++) {
                int indexInText = line.getStartIndex() + glyphIndex;
                if (!missionParsedText.isWhitespace(indexInText)) {
                    float glyphScale = getScale(missionParsedText.getTextStyle(indexInText));
                    float glyphWidth = missionParsedText.getWidth(indexInText) * glyphScale;
                    Entity cardEntity = missionParsedText.getEntity(indexInText);
                    tempMatrix4.set(startupTransform);
                    tempMatrix4.translate(startX + line.getGlyphXAdvance(glyphIndex) + glyphWidth / 2f, 0, startY);
                    tempMatrix4.scl(glyphScale);

                    transformSystem.setTransform(cardEntity, tempMatrix4);
                }
            }
            startY += line.getHeight();
        }

        missionLayout.dispose();
    }

    private static float getScale(TextStyle textStyle) {
        Float scale = (Float) textStyle.getAttribute(TextStyleConstants.GlyphScale);
        return (scale != null) ? scale : 1f;
    }

    private static GlyphOffsetText layoutMissionLines(GlyphOffseter glyphOffseter, CardZoneParsedText missionParsedText,
                                                      float maximumScale, float availableWidth, float availableHeight) {
        float scale = maximumScale;
        while (true) {
            float scaledWidth = availableWidth / scale;

            GlyphOffsetText offsetText = glyphOffseter.offsetText(missionParsedText, scaledWidth, true);
            if (offsetText.getTextWidth() * scale <= availableWidth && offsetText.getTextHeight() * scale <= availableHeight)
                return offsetText;
            scale /= 1.05f;
        }
    }

    private static float calculateScale(GlyphOffsetText offsetText, float width, float height) {
        return Math.min(width / offsetText.getTextWidth(), height / offsetText.getTextHeight());
    }
}
