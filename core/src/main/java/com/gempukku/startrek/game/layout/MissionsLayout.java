package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.game.MissionCards;
import com.gempukku.startrek.game.PlayerCards;
import com.gempukku.startrek.game.PlayerPosition;

public class MissionsLayout {
    private static final float MAXIMUM_SCALE = 1.3f;

    private static final float STACK_HORIZONTAL_GAP = 0.1f;
    private static final float STACK_VERTICAL_GAP = 0.1f;
    private static final float STACK_WIDTH = 0.715257f;
    private static final float STACK_HEIGHT = 0.497570f;
    private static final float STACK_STICKOUT_PERC = 0.2f;

    private static final float MISSION_SPACE_WIDTH = 2f;
    private static final float MISSION_SPACE_HEIGHT = 1.8f;
    private static final float MISSION_SPACE_GAP = 0.2f;
    private static final float MISSION_CENTER_Y_DISTANCE = 0.1f;
    private static final float MISSION_CENTER_Z_DISTANCE = 1.2f;

    public static void layoutMissions(PlayerCards playerCards, PlayerPosition playerPosition,
                                      TransformSystem transformSystem) {
        Matrix4 missionTransform = new Matrix4();
        Matrix4 tempMatrix4 = new Matrix4();
        for (int i = 0; i < 5; i++) {
            MissionCards missionCards = playerCards.getMissionCards(i);

            MissionLayout missionLayout = layoutMissionLines(missionCards);
            int lineCount = missionLayout.missionLines.size;
            float unscaledHeight = STACK_HEIGHT * lineCount + STACK_VERTICAL_GAP * (lineCount - 1);
            float unscaledWidth = getMaxWidth(missionLayout);

            float scaleBasedOnLayout = Math.min(MISSION_SPACE_WIDTH / unscaledWidth, MISSION_SPACE_HEIGHT / unscaledHeight);
            float scale = Math.min(MAXIMUM_SCALE, scaleBasedOnLayout);

            float scaledHeight = unscaledHeight * scale;
            float scaledWidth = unscaledWidth * scale;

            float verticalTranslate = (playerPosition == PlayerPosition.Lower) ? MISSION_CENTER_Z_DISTANCE : -MISSION_CENTER_Z_DISTANCE;
            float horizontalTranslate = (i - 2) * (MISSION_SPACE_WIDTH + MISSION_SPACE_GAP);
            float yRotateDegrees = (playerPosition == PlayerPosition.Lower) ? 0f : 180f;

            // Move to mission center
            missionTransform.idt()
                    .translate(0, MISSION_CENTER_Y_DISTANCE, verticalTranslate)
                    .rotate(new Vector3(0, 1, 0), yRotateDegrees)
                    .translate(horizontalTranslate, 0, 0);
            // Scale the cards
            missionTransform.scl(scale);

            Entity renderedMissionCard = missionCards.getRenderedCard(missionCards.getMissionCard());

            transformSystem.setTransform(renderedMissionCard, missionTransform);
        }
    }

    private static float getMaxWidth(MissionLayout missionLayout) {
        float maxWidth = 0;
        for (MissionLine missionLine : missionLayout.missionLines) {
            maxWidth = Math.max(maxWidth, missionLine.width);
        }
        return maxWidth;
    }

    private static float getRowWidth(float cardGapWidth, Array<Entity> topLevelCards) {
        return topLevelCards.size
                + cardGapWidth * (topLevelCards.size - 1);
    }

    private static MissionLayout layoutMissionLines(MissionCards missionCards) {
        Array<Entity> missionCardArray = new Array<>();
        missionCardArray.add(missionCards.getMissionCard());

        MissionLayout missionLayout = new MissionLayout();
        float scale = MAXIMUM_SCALE;
        while (true) {
            float scaledWidth = MISSION_SPACE_WIDTH / scale;
            if (layoutLinesForCards(missionCards, missionCards.getOpponentTopLevelCardsInMission(),
                    missionLayout, scaledWidth)
                    && layoutLinesForCards(missionCards, missionCardArray,
                    missionLayout, scaledWidth)
                    && layoutLinesForCards(missionCards, missionCards.getPlayerTopLevelCardsInMission(),
                    missionLayout, scaledWidth)) {
                return missionLayout;
            } else {
                Pools.freeAll(missionLayout.missionLines);
                missionLayout.missionLines.clear();
            }
        }
    }

    private static boolean layoutLinesForCards(MissionCards missionCards, Array<Entity> topLevelCards,
                                               MissionLayout missionLayout, float width) {
        MissionLine missionLine = Pools.obtain(MissionLine.class);
        for (Entity topLevelCard : topLevelCards) {
            float stackWidth = getStackWidth(missionCards, topLevelCard);
            if (stackWidth > width)
                // Can't layout whole card stack in the provided width
                return false;
            boolean needsGap = missionLine.width > 0;
            // Check if we need to go to next line
            if (missionLine.width + (needsGap ? STACK_HORIZONTAL_GAP : 0) + stackWidth > width) {
                missionLayout.missionLines.add(missionLine);
                missionLine = Pools.obtain(MissionLine.class);
                needsGap = false;
            }
            missionLine.topLevelEntities.add(topLevelCard);
            missionLine.width += (needsGap ? STACK_HORIZONTAL_GAP : 0) + stackWidth;
        }
        if (missionLine.topLevelEntities.size > 0)
            missionLayout.missionLines.add(missionLine);
        return true;
    }

    private static float getStackWidth(MissionCards missionCards, Entity topLevelCard) {
        return STACK_WIDTH + missionCards.getAttachedCards(topLevelCard).size * (STACK_WIDTH * STACK_STICKOUT_PERC);
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
