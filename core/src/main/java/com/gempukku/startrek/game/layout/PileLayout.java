package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.render.zone.PlayerZones;
import com.gempukku.startrek.game.render.zone.RenderedCardGroup;

public class PileLayout {
    private static final float[] deckRotation = new float[]{0f, -1f, 0.3f, 0.1f, -0.3f, -0.1f, 0f, 0.15f};

    public static void layoutPlayerDilemmaPile(
            RenderedCardGroup dilemmaPileCards, PlayerPosition playerPosition,
            TransformSystem transformSystem) {
        float xTranslate = -4f;
        float zTranslate = 3.3f;
        Array<Entity> cardsInDilemmaPile = dilemmaPileCards.getRenderedCards();
        for (int i = 0; i < cardsInDilemmaPile.size; i++) {
            Entity cardRepresentation = cardsInDilemmaPile.get(i);

            float yRotation = (playerPosition == PlayerPosition.Lower) ? 0 : 180;
            yRotation += 3 * deckRotation[i];
            float zMove = playerPosition == PlayerPosition.Lower ? zTranslate : -zTranslate;

            transformSystem.setTransform(cardRepresentation,
                    new Matrix4().idt()
                            .translate(xTranslate, 0f + i * 0.005f, zMove)
                            // A bit crooked
                            .rotate(0, 1, 0, yRotation));
        }
    }

    public static void layoutPlayerDeck(
            RenderedCardGroup deckCards, PlayerPosition playerPosition,
            TransformSystem transformSystem) {
        float xTranslate = 4f;
        float zTranslate = 3.3f;
        Array<Entity> cardsInDeck = deckCards.getRenderedCards();
        for (int i = 0; i < cardsInDeck.size; i++) {
            Entity cardRepresentation = cardsInDeck.get(i);

            float yRotation = (playerPosition == PlayerPosition.Lower) ? 0 : 180;
            yRotation += 3 * deckRotation[i];
            float zMove = playerPosition == PlayerPosition.Lower ? zTranslate : -zTranslate;

            transformSystem.setTransform(cardRepresentation,
                    new Matrix4().idt()
                            .translate(xTranslate, 0f + i * 0.005f, zMove)
                            // A bit crooked
                            .rotate(0, 1, 0, yRotation));
        }
    }

    public static void layoutPlayerDiscardPile(
            PlayerZones playerZones, PlayerPosition playerPosition,
            TransformSystem transformSystem) {
        float xTranslate = 4.5f;
        float zTranslate = 3.3f;
        Entity cardRepresentation = playerZones.getTopDiscardPileCard();
        if (cardRepresentation != null) {
            float yRotation = (playerPosition == PlayerPosition.Lower) ? 0 : 180;
            float zMove = playerPosition == PlayerPosition.Lower ? zTranslate : -zTranslate;

            transformSystem.setTransform(cardRepresentation,
                    new Matrix4().idt()
                            .translate(xTranslate, 0f, zMove)
                            // A bit crooked
                            .rotate(0, 1, 0, yRotation));
        }
    }
}
