package com.gempukku.startrek.game.zone;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.ServerStateChanged;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.PlayerZones;
import com.gempukku.startrek.game.template.CardTemplates;

public class FaceDownCardTrackingSystem extends BaseSystem {
    private AuthenticationHolderSystem authenticationHolderSystem;
    private PlayerPositionSystem playerPositionSystem;
    private CardRenderingSystem cardRenderingSystem;
    private SpawnSystem spawnSystem;
    private TransformSystem transformSystem;

    private boolean stateChanged = false;

    @EventListener
    public void serverStateChanged(ServerStateChanged serverStateChanged, Entity entity) {
        stateChanged = true;
    }

    @Override
    protected void processSystem() {
        if (stateChanged) {
            stateChanged = false;

            setupUnknownPlayersHands();
            setupPlayersDecks();
            setupPlayersDilemmaPiles();
        }
    }

    private void setupPlayersDecks() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            PlayerPosition playerPosition = player.value;
            Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
            PlayerZones playerZones = cardRenderingSystem.getPlayerCards(playerPosition);

            PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            int deckCount = getRenderedDeckSize(publicStats.getDeckCount());
            int renderedCount = playerZones.getCardInDeckCount();
            if (renderedCount > deckCount) {
                int destroyCount = renderedCount - deckCount;
                for (int i = 0; i < destroyCount; i++) {
                    Entity removedCard = playerZones.removeOneCardInDeck();
                    world.deleteEntity(removedCard);
                }
            } else if (renderedCount < deckCount) {
                int createCount = deckCount - renderedCount;
                for (int i = 0; i < createCount; i++) {
                    addFaceDownCardToDeck(playerZones);
                }
            }
        }
    }

    private void setupPlayersDilemmaPiles() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            PlayerPosition playerPosition = player.value;
            Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
            PlayerZones playerZones = cardRenderingSystem.getPlayerCards(playerPosition);

            PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            int deckCount = getRenderedDeckSize(publicStats.getDilemmaCount());
            int renderedCount = playerZones.getCardInDilemmaCount();
            if (renderedCount > deckCount) {
                int destroyCount = renderedCount - deckCount;
                for (int i = 0; i < destroyCount; i++) {
                    Entity removedCard = playerZones.removeOneCardInDilemmaPile();
                    world.deleteEntity(removedCard);
                }
            } else if (renderedCount < deckCount) {
                int createCount = deckCount - renderedCount;
                for (int i = 0; i < createCount; i++) {
                    addFaceDownCardToDilemmaPile(playerZones);
                }
            }
        }
    }

    private void addFaceDownCardToDeck(PlayerZones playerZones) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        playerZones.addCardInDeck(null, cardRepresentation);
    }

    private void addFaceDownCardToDilemmaPile(PlayerZones playerZones) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        playerZones.addCardInDilemmaPile(null, cardRepresentation);
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

    private void setupUnknownPlayersHands() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            if (!username.equals(authenticationHolderSystem.getUsername())) {
                PlayerPosition playerPosition = player.value;
                PlayerZones playerZones = cardRenderingSystem.getPlayerCards(playerPosition);
                Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
                layoutPlayerUnknownHand(playerPosition, playerZones, playerEntity);
            }
        }
    }

    private void layoutPlayerUnknownHand(PlayerPosition playerPosition, PlayerZones playerZones, Entity playerEntity) {
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        int handCount = publicStats.getHandCount();
        int renderedCount = playerZones.getCardInHandCount();
        if (renderedCount > handCount) {
            int destroyCount = renderedCount - handCount;
            for (int i = 0; i < destroyCount; i++) {
                Entity removedCard = playerZones.removeOneCardInHand();
                world.deleteEntity(removedCard);
            }
        } else if (renderedCount < handCount) {
            int createCount = handCount - renderedCount;
            for (int i = 0; i < createCount; i++) {
                addFaceDownCardToHand(playerPosition);
            }
        }
    }

    private void addFaceDownCardToHand(PlayerPosition playerPosition) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        cardRenderingSystem.getPlayerCards(playerPosition).addCardInHand(null, cardRepresentation);
    }
}
