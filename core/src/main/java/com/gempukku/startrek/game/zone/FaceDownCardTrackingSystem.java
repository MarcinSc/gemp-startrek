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
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.MissionCards;
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

            updateUnknownPlayersHands();
            updatePlayersDecks();
            updatePlayersDilemmaPiles();
            updatePlayersMissions();
        }
    }

    private void updatePlayersMissions() {
        for (ObjectMap.Entry<String, PlayerPosition> missionPlayer : new ObjectMap.Entries<>(playerPositionSystem.getPlayerPositions())) {
            String missionOwner = missionPlayer.key;
            PlayerPosition missionPlayerPosition = missionPlayer.value;
            PlayerZones missionPlayerZones = cardRenderingSystem.getPlayerCards(missionPlayerPosition);

            for (int missionIndex = 0; missionIndex < 5; missionIndex++) {
                Entity missionEntity = MissionOperations.findMission(world, missionOwner, missionIndex);
                MissionComponent mission = missionEntity.getComponent(MissionComponent.class);

                MissionCards missionCards = missionPlayerZones.getMissionCards(missionIndex);
                updateFaceDownMissionCards(missionOwner, mission, missionCards);
            }
        }
    }

    private void updateFaceDownMissionCards(String missionOwner, MissionComponent mission, MissionCards missionCards) {
        int playerFaceDownCardCount = 0;
        int opponentFaceDownCardCount = 0;
        for (ObjectMap.Entry<String, PlayerPosition> cardOwnerEntry : playerPositionSystem.getPlayerPositions()) {
            String ownerUsername = cardOwnerEntry.key;
            if (!ownerUsername.equals(authenticationHolderSystem.getUsername())) {
                if (missionOwner.equals(ownerUsername)) {
                    playerFaceDownCardCount += mission.getPlayerFaceDownCardsCount().get(ownerUsername, 0);
                } else {
                    opponentFaceDownCardCount += mission.getPlayerFaceDownCardsCount().get(ownerUsername, 0);
                }
            }
        }

        int existingPlayerFaceDownCards = missionCards.getFaceDownPlayerCardCount();
        if (existingPlayerFaceDownCards > playerFaceDownCardCount) {
            int destroyCount = existingPlayerFaceDownCards - playerFaceDownCardCount;
            for (int i = 0; i < destroyCount; i++) {
                Entity removedCard = missionCards.removeFaceDownPlayerCard();
                world.deleteEntity(removedCard);
            }

        } else if (existingPlayerFaceDownCards < playerFaceDownCardCount) {
            int createCount = playerFaceDownCardCount - existingPlayerFaceDownCards;
            for (int i = 0; i < createCount; i++) {
                addFaceDownPlayerCard(missionCards);
            }
        }

        int existingOpponentFaceDownCards = missionCards.getFaceDownOpponentCardCount();
        if (existingOpponentFaceDownCards > opponentFaceDownCardCount) {
            int destroyCount = existingOpponentFaceDownCards - opponentFaceDownCardCount;
            for (int i = 0; i < destroyCount; i++) {
                Entity removedCard = missionCards.removeFaceDownOpponentCard();
                world.deleteEntity(removedCard);
            }

        } else if (existingOpponentFaceDownCards < opponentFaceDownCardCount) {
            int createCount = opponentFaceDownCardCount - existingOpponentFaceDownCards;
            for (int i = 0; i < createCount; i++) {
                addFaceDownOpponentCard(missionCards);
            }
        }
    }

    private void updatePlayersDecks() {
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

    private void updatePlayersDilemmaPiles() {
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

    private void addFaceDownPlayerCard(MissionCards missionCards) {
        Entity cardRepresentation = CardTemplates.createSmallFaceDownCard(spawnSystem);
        missionCards.addPlayerTopLevelCardInMission(null, cardRepresentation);
    }

    private void addFaceDownOpponentCard(MissionCards missionCards) {
        Entity cardRepresentation = CardTemplates.createSmallFaceDownCard(spawnSystem);
        missionCards.addOpponentTopLevelCardInMission(null, cardRepresentation);
    }

    private void addFaceDownCardToDeck(PlayerZones playerZones) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        playerZones.addCardInDeck(cardRepresentation);
    }

    private void addFaceDownCardToDilemmaPile(PlayerZones playerZones) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        playerZones.addCardInDilemmaPile(cardRepresentation);
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

    private void updateUnknownPlayersHands() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            if (!username.equals(authenticationHolderSystem.getUsername())) {
                PlayerPosition playerPosition = player.value;
                PlayerZones playerZones = cardRenderingSystem.getPlayerCards(playerPosition);
                Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
                updatePlayerUnknownHand(playerPosition, playerZones, playerEntity);
            }
        }
    }

    private void updatePlayerUnknownHand(PlayerPosition playerPosition, PlayerZones playerZones, Entity playerEntity) {
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
