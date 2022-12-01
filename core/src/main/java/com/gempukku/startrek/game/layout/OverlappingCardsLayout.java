package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.text.TextHorizontalAlignment;
import com.gempukku.libgdx.lib.graph.artemis.text.TextVerticalAlignment;

public class OverlappingCardsLayout {

    private static final Matrix4 tmpMatrix = new Matrix4();

    public static void layoutCards(TransformSystem transformSystem, Matrix4 startupTransform,
                                   Array<Entity> cards, float cardWidth, float cardHeight, float scale,
                                   float minCardOverlapPerc, float availableWidth, float availableHeight) {

        int cardCount = cards.size;

        float cardShift = cardWidth * minCardOverlapPerc;
        if (cardCount > 1) {
            cardShift = Math.min(cardShift, (availableWidth - cardWidth) / (cardCount - 1));
        }

        float usedWidth = cardWidth + cardShift * (cards.size - 1);

        float startY = TextVerticalAlignment.center.apply(cardHeight, availableHeight) - availableHeight / 2f;
        float startX = TextHorizontalAlignment.center.apply(usedWidth, availableWidth) - availableWidth / 2f;

        startY += cardHeight / 2f;
        startX += cardWidth / 2f;

        for (Entity card : cards) {
            tmpMatrix.set(startupTransform);
            tmpMatrix.translate(startX, 0, startY);
            tmpMatrix.rotate(0, 0, 1, -4f);
            tmpMatrix.scl(scale, 1, scale);

            transformSystem.setTransform(card, tmpMatrix);

            startX += cardShift;
        }
    }
}
