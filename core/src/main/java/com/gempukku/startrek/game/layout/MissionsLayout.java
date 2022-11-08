package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.game.MissionCards;
import com.gempukku.startrek.game.PlayerCards;
import com.gempukku.startrek.game.PlayerPosition;

public class MissionsLayout {
    public static void layoutMissions(PlayerCards playerCards, PlayerPosition playerPosition,
                                      TransformSystem transformSystem) {
        float cardGapWidth = 0.1f;
        float stackOverlap = 0.8f;
        float[] missionCardWidths = new float[5];
        for (int i = 0; i < 5; i++) {
            MissionCards missionCards = playerCards.getMissionCards(i);
            // Check opponent cards
            Array<Entity> opponentTopLevelCards = missionCards.getOpponentTopLevelCardsInMission();
            float opponentRowWidth = getRowWidth(cardGapWidth, opponentTopLevelCards);
            // Check mission card
            float missionRowWidth = 1f;
            Array<Entity> playerTopLevelCards = missionCards.getPlayerTopLevelCardsInMission();
            float playerRowWidth = getRowWidth(cardGapWidth, playerTopLevelCards);
            missionCardWidths[i] = Math.max(Math.max(opponentRowWidth, missionRowWidth), playerRowWidth);
        }

        float vertTrans = 1.2f;
        float horTrans = 2f;
        float yTrans = 0.1f;
        float scale = 1f;
        Matrix4 m4 = new Matrix4();
        for (int i = 0; i < 5; i++) {
            MissionCards missionCards = playerCards.getMissionCards(i);
            Entity missionCard = missionCards.getMissionCard();
            if (missionCard != null) {
                float verticalTranslate = (playerPosition == PlayerPosition.Lower) ? vertTrans : -vertTrans;
                float horizontalTranslate = (i - 2) * horTrans;
                float yRotateDegrees = (playerPosition == PlayerPosition.Lower) ? 0f : 180f;

                m4.idt()
                        .translate(0, yTrans, verticalTranslate)
                        .rotate(new Vector3(0, 1, 0), yRotateDegrees)
                        .translate(horizontalTranslate, 0, 0)
                        .scl(scale);

                transformSystem.setTransform(missionCard, m4);
            }
        }
    }

    private static float getRowWidth(float cardGapWidth, Array<Entity> topLevelCards) {
        return topLevelCards.size
                + cardGapWidth * (topLevelCards.size - 1);
    }
}
