package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.zone.PlayerZones;

public class HandLayout {
    private static final float VERTICAL_DISTANCE = 0.3f;//0.85f;
    private static final float DISTANCE_FROM_CAMERA = 3f;
    private static final float CARD_SEPARATION = 0.15f;
    private static final float CARD_SCALE = 0.4f;

    public static void layoutHand(PlayerZones playerZones, PlayerPosition playerPosition,
                                  Camera camera, TransformSystem transformSystem) {
        Array<Entity> cardsInHand = playerZones.getCardsInHand();

        Vector3 basePlayerHandPosition =
                new Vector3(camera.position)
                        .add(new Vector3(camera.direction).scl(DISTANCE_FROM_CAMERA))
                        .add(new Vector3(camera.up).scl(-VERTICAL_DISTANCE));
        Vector3 baseOpponentHandPosition =
                new Vector3(camera.position)
                        .add(new Vector3(camera.direction).scl(DISTANCE_FROM_CAMERA))
                        .add(new Vector3(camera.up).scl(VERTICAL_DISTANCE));

        Vector3 playerHandPosition = (playerPosition == PlayerPosition.Lower) ? basePlayerHandPosition : baseOpponentHandPosition;

        int index = 0;
        int handSize = cardsInHand.size;
        for (Entity cardInHand : cardsInHand) {
            float indexBias = index - (handSize / 2f) + 0.5f;
            float rotateX = 10;
            float rotateY = (playerPosition == PlayerPosition.Lower) ? -indexBias * 1.5f : 180 + indexBias * 1.5f;
            float rotateZ = (playerPosition == PlayerPosition.Lower) ? -2 : 2;
            transformSystem.setTransform(cardInHand, new Matrix4()
                    .translate(playerHandPosition.x + CARD_SEPARATION * indexBias, playerHandPosition.y, playerHandPosition.z)// + 0.005f * Math.abs(indexBias))
                    .scl(CARD_SCALE)
                    .rotate(1, 0, 0, rotateX)
                    .rotate(0, 1, 0, rotateY)
                    .rotate(0, 0, 1, rotateZ));

            index++;
        }
    }
}
