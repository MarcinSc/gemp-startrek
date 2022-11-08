package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.TextHorizontalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.text.TextVerticalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.DefaultGlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffsetLine;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffsetText;
import com.gempukku.libgdx.lib.graph.artemis.text.layout.GlyphOffseter;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.ParsedText;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyle;
import com.gempukku.libgdx.lib.graph.artemis.text.parser.TextStyleConstants;
import com.gempukku.startrek.game.MissionCards;
import com.gempukku.startrek.game.PlayerCards;
import com.gempukku.startrek.game.PlayerPosition;

public class MissionsLayout {
    private static final float MAXIMUM_SCALE = 1.3f;

    private static final float STACK_HORIZONTAL_GAP = 0.03f;
    private static final float STACK_VERTICAL_GAP = 0.03f;
    private static final float STACK_WIDTH = 0.715257f;
    private static final float STACK_HEIGHT = 0.497570f;
    private static final float STACK_STICKOUT_PERC = 0.2f;

    private static final float MISSION_SPACE_WIDTH = 2f;
    private static final float MISSION_SPACE_HEIGHT = 1.55f;
    private static final float MISSION_SPACE_GAP = 0.2f;
    private static final float MISSION_CENTER_Y_DISTANCE = 0.005f;
    private static final float MISSION_CENTER_Z_DISTANCE = 1.6f;

    public static void layoutMissions(PlayerCards playerCards, PlayerPosition playerPosition,
                                      TransformSystem transformSystem) {
        DefaultGlyphOffseter defaultGlyphOffseter = new DefaultGlyphOffseter();

        Matrix4 missionTransform = new Matrix4();
        Matrix4 tempMatrix4 = new Matrix4();
        for (int i = 0; i < 5; i++) {
            MissionCards missionCards = playerCards.getMissionCards(i);

            MissionParsedText missionParsedText = new MissionParsedText(missionCards);

            GlyphOffsetText missionLayout = layoutMissionLines(defaultGlyphOffseter, missionParsedText);
            float scale = Math.min(MAXIMUM_SCALE, calculateScale(missionLayout, MISSION_SPACE_WIDTH, MISSION_SPACE_HEIGHT));

            float verticalTranslate = (playerPosition == PlayerPosition.Lower) ?
                    MISSION_CENTER_Z_DISTANCE : -MISSION_CENTER_Z_DISTANCE;
            float horizontalTranslate = (i - 2) * (MISSION_SPACE_WIDTH + MISSION_SPACE_GAP);
            float yRotateDegrees = (playerPosition == PlayerPosition.Lower) ? 0f : 180f;

            // Move to mission center
            missionTransform.idt()
                    .translate(0, MISSION_CENTER_Y_DISTANCE, verticalTranslate)
                    .rotate(new Vector3(0, 1, 0), yRotateDegrees)
                    .translate(horizontalTranslate, 0, 0);
            // Scale the cards
            missionTransform.scale(scale, 1f, scale);

            float startY = TextVerticalAlignment.center.apply(missionLayout.getTextHeight(), MISSION_SPACE_HEIGHT) - MISSION_SPACE_HEIGHT / 2f;
            for (int lineIndex = 0; lineIndex < missionLayout.getLineCount(); lineIndex++) {
                GlyphOffsetLine line = missionLayout.getLine(lineIndex);
                float lineWidth = line.getWidth();
                float startX = TextHorizontalAlignment.center.apply(lineWidth, MISSION_SPACE_WIDTH) - MISSION_SPACE_WIDTH / 2f;
                for (int glyphIndex = 0; glyphIndex < line.getGlyphCount(); glyphIndex++) {
                    int indexInText = line.getStartIndex() + glyphIndex;
                    if (!missionParsedText.isWhitespace(indexInText)) {
                        float glyphScale = getScale(missionParsedText.getTextStyle(indexInText));
                        float glyphWidth = missionParsedText.getWidth(indexInText) * glyphScale;
                        Entity entity = missionParsedText.getEntity(indexInText);
                        tempMatrix4.set(missionTransform);
                        tempMatrix4.translate(startX + line.getGlyphXAdvance(glyphIndex) + glyphWidth / 2f, 0, startY);
                        tempMatrix4.scl(glyphScale);

                        Entity renderedMissionCard = missionCards.getRenderedCard(entity);
                        transformSystem.setTransform(renderedMissionCard, tempMatrix4);
                    }
                }
                startY += line.getHeight();
            }

            missionLayout.dispose();
        }
    }

    private static float getScale(TextStyle textStyle) {
        Float scale = (Float) textStyle.getAttribute(TextStyleConstants.GlyphScale);
        return (scale != null) ? scale : 1f;
    }

    private static GlyphOffsetText layoutMissionLines(GlyphOffseter glyphOffseter, MissionParsedText missionParsedText) {
        float scale = MAXIMUM_SCALE;
        while (true) {
            float scaledWidth = MISSION_SPACE_WIDTH / scale;

            GlyphOffsetText offsetText = glyphOffseter.offsetText(missionParsedText, scaledWidth, true);
            if (offsetText.getTextWidth() * scale <= MISSION_SPACE_WIDTH && offsetText.getTextHeight() * scale <= MISSION_SPACE_HEIGHT)
                return offsetText;
            scale /= 1.05f;
        }
    }

    private static float calculateScale(GlyphOffsetText offsetText, float width, float height) {
        return Math.min(width / offsetText.getTextWidth(), height / offsetText.getTextHeight());
    }

    private static float getStackWidth(MissionCards missionCards, Entity topLevelCard) {
        return STACK_WIDTH + missionCards.getAttachedCards(topLevelCard).size * (STACK_WIDTH * STACK_STICKOUT_PERC);
    }

    private static class MissionParsedText implements ParsedText {
        private static final Object spaceGlyph = new Object();
        private static final Object lineBreakGlyph = new Object();
        private Array<Object> glyphs = new Array<>();
        private Array<TextStyle> textStyles = new Array<>();

        private TextStyle normalTextStyle;
        private TextStyle missionTextStyle;
        private MissionCards missionCards;

        public MissionParsedText(MissionCards missionCards) {
            this.missionCards = missionCards;

            normalTextStyle = new TextStyle();
            normalTextStyle.setAttribute(TextStyleConstants.AlignmentVertical, TextVerticalAlignment.center);
            normalTextStyle.setAttribute(TextStyleConstants.AlignmentHorizontal, TextHorizontalAlignment.center);
            normalTextStyle.setAttribute(TextStyleConstants.LineSpacing, STACK_VERTICAL_GAP);

            missionTextStyle = normalTextStyle.duplicate();
            missionTextStyle.setAttribute(TextStyleConstants.GlyphScale, 1.2f);

            addCards(missionCards.getOpponentTopLevelCardsInMission());
            addGlyphs(missionCards.getMissionCard(), missionTextStyle);
            addGlyphs(lineBreakGlyph, normalTextStyle);
            addCards(missionCards.getPlayerTopLevelCardsInMission());
        }

        private void addCards(Array<Entity> topLevelCards) {
            boolean first = true;
            for (Entity entity : topLevelCards) {
                if (!first) {
                    addGlyphs(spaceGlyph, normalTextStyle);
                }
                addGlyphs(entity, normalTextStyle);
                first = false;
            }
            if (topLevelCards.size > 0) {
                addGlyphs(lineBreakGlyph, normalTextStyle);
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
            return STACK_HEIGHT;
        }

        @Override
        public float getWidth(int glyphIndex) {
            Object glyph = glyphs.get(glyphIndex);
            if (glyph == spaceGlyph)
                return STACK_HORIZONTAL_GAP;
            return getStackWidth(missionCards, (Entity) glyph);
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

    private static class MissionLayout {
        private Array<MissionLine> missionLines = new Array<>();
    }

    private static class MissionLine implements Pool.Poolable {
        private float width;
        private Array<Entity> topLevelEntities = new Array<>();

        @Override
        public void reset() {
            width = 0;
            topLevelEntities.clear();
        }
    }
}
