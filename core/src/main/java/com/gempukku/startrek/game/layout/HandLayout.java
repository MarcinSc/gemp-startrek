package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.game.PlayerCards;
import com.gempukku.startrek.game.PlayerPosition;

public class HandLayout {
    public static void layoutHand(PlayerCards playerCards, PlayerPosition playerPosition,
                                  Camera camera, TransformSystem transformSystem) {
        Array<Entity> cardsInHand = playerCards.getCardsInHand();

        float verticalScale = 0.85f;
        float distanceFromCamera = 3f;
        float cardSeparation = 0.15f;
        float cardScale = 0.4f;

        Vector3 basePlayerHandPosition =
                new Vector3(camera.position)
                        .add(new Vector3(camera.direction).scl(distanceFromCamera))
                        .add(new Vector3(camera.up).scl(-verticalScale));
        Vector3 baseOpponentHandPosition =
                new Vector3(camera.position)
                        .add(new Vector3(camera.direction).scl(distanceFromCamera))
                        .add(new Vector3(camera.up).scl(verticalScale));

        Vector3 playerHandPosition = (playerPosition == PlayerPosition.Lower) ? basePlayerHandPosition : baseOpponentHandPosition;

        int index = 0;
        int handSize = cardsInHand.size;
        for (Entity cardInHand : cardsInHand) {
            float indexBias = index - (handSize / 2f) + 0.5f;
            float rotateX = 10;
            float rotateY = (playerPosition == PlayerPosition.Lower) ? -indexBias * 1.5f : 180 + indexBias * 1.5f;
            float rotateZ = (playerPosition == PlayerPosition.Lower) ? -2 : 2;
            transformSystem.setTransform(cardInHand, new Matrix4()
                    .translate(playerHandPosition.x + cardSeparation * indexBias, playerHandPosition.y, playerHandPosition.z)// + 0.005f * Math.abs(indexBias))
                    .scale(cardScale, cardScale, cardScale)
                    .rotate(1, 0, 0, rotateX)
                    .rotate(0, 1, 0, rotateY)
                    .rotate(0, 0, 1, rotateZ));

            index++;
        }
    }
}
