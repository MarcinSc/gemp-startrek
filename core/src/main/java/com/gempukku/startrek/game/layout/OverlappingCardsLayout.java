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
                                   float availableWidth, float availableHeight, float minCardOverlapPerc,
                                   boolean vertical) {
        if (vertical) {
            layoutCardsVertical(transformSystem, startupTransform, cards, cardWidth, cardHeight, scale, availableWidth, availableHeight, minCardOverlapPerc);
        } else {
            layoutCardsHorizontal(transformSystem, startupTransform, cards, cardWidth, cardHeight, scale, availableWidth, availableHeight, minCardOverlapPerc);
        }
    }

    private static void layoutCardsVertical(TransformSystem transformSystem, Matrix4 startupTransform, Array<Entity> cards, float cardWidth, float cardHeight, float scale, float availableWidth, float availableHeight, float minCardOverlapPerc) {
        int cardCount = cards.size;

        float cardShift = cardHeight * minCardOverlapPerc;
        if (cardCount > 1) {
            cardShift = Math.min(cardShift, (availableHeight - cardHeight) / (cardCount - 1));
        }

        float usedHeight = cardHeight + cardShift * (cards.size - 1);

        float startY = TextVerticalAlignment.center.apply(usedHeight, availableHeight) - availableHeight / 2f;
        float startX = TextHorizontalAlignment.center.apply(cardWidth, availableWidth) - availableWidth / 2f;

        startY += cardHeight / 2f;
        startX += cardWidth / 2f;

        for (Entity card : cards) {
            tmpMatrix.set(startupTransform);
            tmpMatrix.translate(startX, 0, startY);
            tmpMatrix.rotate(1, 0, 0, 4f);
            tmpMatrix.scl(scale, 1, scale);

            transformSystem.setTransform(card, tmpMatrix);

            startY += cardShift;
        }
    }

    private static void layoutCardsHorizontal(TransformSystem transformSystem, Matrix4 startupTransform, Array<Entity> cards, float cardWidth, float cardHeight, float scale, float availableWidth, float availableHeight, float minCardOverlapPerc) {
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
