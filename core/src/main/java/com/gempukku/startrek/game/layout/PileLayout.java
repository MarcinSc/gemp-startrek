package com.gempukku.startrek.game.layout;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Matrix4;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.game.PlayerCards;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.template.CardTemplates;

public class PileLayout {
    private static final float[] deckRotation = new float[]{0f, -1f, 0.3f, 0.1f, -0.3f, -0.1f, 0f, 0.15f};

    public static void layoutPlayerDilemmaPile(
            PlayerCards playerCards, PlayerPosition playerPosition,
            Entity playerEntity, World world,
            SpawnSystem spawnSystem, TransformSystem transformSystem) {
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        int deckCount = getRenderedDeckSize(publicStats.getDilemmaCount());
        int renderedCount = playerCards.getCardInDilemmaCount();
        if (renderedCount > deckCount) {
            int destroyCount = renderedCount - deckCount;
            for (int i = 0; i < destroyCount; i++) {
                Entity removedCard = playerCards.removeOneCardInDilemmaPile();
                world.deleteEntity(removedCard);
            }
        } else if (renderedCount < deckCount) {
            float xTranslate = -4f;
            float zTranslate = 3.3f;
            int createCount = deckCount - renderedCount;
            for (int i = 0; i < createCount; i++) {
                Entity cardRepresentation = addFaceDownCardToDilemmaPile(playerCards, spawnSystem);
                int cardIndex = playerCards.getCardInDilemmaCount() - 1;

                float yRotation = (playerPosition == PlayerPosition.Lower) ? 0 : 180;
                yRotation += 3 * deckRotation[cardIndex];
                float zMove = playerPosition == PlayerPosition.Lower ? zTranslate : -zTranslate;

                transformSystem.setTransform(cardRepresentation,
                        new Matrix4().idt()
                                .translate(xTranslate, 0f + cardIndex * 0.005f, zMove)
                                // A bit crooked
                                .rotate(0, 1, 0, yRotation));
            }
        }
    }

    public static void layoutPlayerDeck(
            PlayerCards playerCards, PlayerPosition playerPosition,
            Entity playerEntity, World world,
            SpawnSystem spawnSystem, TransformSystem transformSystem) {
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        int deckCount = getRenderedDeckSize(publicStats.getDeckCount());
        int renderedCount = playerCards.getCardInDeckCount();
        if (renderedCount > deckCount) {
            int destroyCount = renderedCount - deckCount;
            for (int i = 0; i < destroyCount; i++) {
                Entity removedCard = playerCards.removeOneCardInDeck();
                world.deleteEntity(removedCard);
            }
        } else if (renderedCount < deckCount) {
            float xTranslate = 4f;
            float zTranslate = 3.3f;
            int createCount = deckCount - renderedCount;
            for (int i = 0; i < createCount; i++) {
                Entity cardRepresentation = addFaceDownCardToDeck(playerCards, spawnSystem);
                int cardIndex = playerCards.getCardInDeckCount() - 1;

                float yRotation = (playerPosition == PlayerPosition.Lower) ? 0 : 180;
                yRotation += 3 * deckRotation[cardIndex];
                float zMove = playerPosition == PlayerPosition.Lower ? zTranslate : -zTranslate;

                transformSystem.setTransform(cardRepresentation,
                        new Matrix4().idt()
                                .translate(xTranslate, 0f + cardIndex * 0.005f, zMove)
                                // A bit crooked
                                .rotate(0, 1, 0, yRotation));
            }
        }
    }

    private static Entity addFaceDownCardToDeck(PlayerCards playerCards, SpawnSystem spawnSystem) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        playerCards.addCardInDeck(null, cardRepresentation);
        return cardRepresentation;
    }

    private static Entity addFaceDownCardToDilemmaPile(PlayerCards playerCards, SpawnSystem spawnSystem) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        playerCards.addCardInDilemmaPile(null, cardRepresentation);
        return cardRepresentation;
    }

    private static int getRenderedDeckSize(int realDeckSize) {
        if (realDeckSize > 20)
            return 8;
        if (realDeckSize > 10)
            return 7;
        if (realDeckSize > 5)
            return 6;
        return realDeckSize;
    }
}
