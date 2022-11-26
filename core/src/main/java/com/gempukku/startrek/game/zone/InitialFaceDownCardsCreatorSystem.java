package com.gempukku.startrek.game.zone;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.ServerStateChanged;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.MissionCards;
import com.gempukku.startrek.game.render.zone.PlayerZones;
import com.gempukku.startrek.game.render.zone.RenderedCardGroup;
import com.gempukku.startrek.game.template.CardTemplates;

public class InitialFaceDownCardsCreatorSystem extends BaseSystem {
    private AuthenticationHolderSystem authenticationHolderSystem;
    private PlayerPositionSystem playerPositionSystem;
    private CardRenderingSystem cardRenderingSystem;
    private SpawnSystem spawnSystem;
    private TransformSystem transformSystem;
    private MissionOperations missionOperations;

    private boolean initializedState = false;
    private boolean processState = false;

    @EventListener
    public void serverStateChanged(ServerStateChanged serverStateChanged, Entity entity) {
        if (!initializedState) {
            processState = true;
            initializedState = true;
        }
    }

    @Override
    protected void processSystem() {
        if (processState) {
            processServerState();
            processState = false;
        }
    }

    private void processServerState() {
        updateUnknownPlayersHands();
        updatePlayersDecks();
        updatePlayersDilemmaPiles();
        updatePlayersMissions();
    }

    private void updatePlayersMissions() {
        for (ObjectMap.Entry<String, PlayerPosition> missionPlayer : new ObjectMap.Entries<>(playerPositionSystem.getPlayerPositions())) {
            String missionOwner = missionPlayer.key;
            PlayerPosition missionPlayerPosition = missionPlayer.value;
            PlayerZones missionPlayerZones = cardRenderingSystem.getPlayerCards(missionPlayerPosition);

            for (int missionIndex = 0; missionIndex < 5; missionIndex++) {
                Entity missionEntity = missionOperations.findMission(missionOwner, missionIndex);
                MissionComponent mission = missionEntity.getComponent(MissionComponent.class);

                MissionCards missionCards = missionPlayerZones.getMissionCards(missionIndex);
                updateFaceDownMissionCards(missionOwner, mission, missionCards);
            }
        }
    }

    private void updateFaceDownMissionCards(String missionOwner, MissionComponent mission, MissionCards missionCards) {
        // At this point only top level revealed cards are there
        for (Entity renderedCard : missionCards.getMissionOwnerCards().getRenderedCards()) {
            Entity serverCard = world.getEntity(renderedCard.getComponent(ServerCardReferenceComponent.class).getEntityId());
            addAttachedCards(serverCard, missionCards.getMissionOwnerCards());
        }
        for (Entity renderedCard : missionCards.getOpposingCards().getRenderedCards()) {
            Entity serverCard = world.getEntity(renderedCard.getComponent(ServerCardReferenceComponent.class).getEntityId());
            addAttachedCards(serverCard, missionCards.getOpposingCards());
        }

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

        for (int i = 0; i < playerFaceDownCardCount; i++) {
            addFaceDownPlayerCard(missionCards);
        }
        for (int i = 0; i < opponentFaceDownCardCount; i++) {
            addFaceDownOpponentCard(missionCards);
        }
    }

    private void addAttachedCards(Entity topLevelCard, RenderedCardGroup renderedCardGroup) {
        CardInPlayComponent cardInPlay = topLevelCard.getComponent(CardInPlayComponent.class);
        String ownerUsername = topLevelCard.getComponent(CardComponent.class).getOwner();
        // Process face down cards only for players that are not owners of the card
        if (!ownerUsername.equals(authenticationHolderSystem.getUsername())) {
            for (int i = 0; i < cardInPlay.getAttachedFaceDownCount(); i++) {
                Entity cardRepresentation = CardTemplates.createSmallFaceDownCard(spawnSystem);
                cardRenderingSystem.addFaceDownAttachedCard(topLevelCard, cardRepresentation);
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
            for (int i = 0; i < deckCount; i++) {
                addFaceDownCardToDeck(playerZones);
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
            for (int i = 0; i < deckCount; i++) {
                addFaceDownCardToDilemmaPile(playerZones);
            }
        }
    }

    private void addFaceDownPlayerCard(MissionCards missionCards) {
        Entity cardRepresentation = CardTemplates.createSmallFaceDownCard(spawnSystem);
        missionCards.getMissionOwnerCards().addFaceDownCard(cardRepresentation);
    }

    private void addFaceDownOpponentCard(MissionCards missionCards) {
        Entity cardRepresentation = CardTemplates.createSmallFaceDownCard(spawnSystem);
        missionCards.getOpposingCards().addFaceDownCard(cardRepresentation);
    }

    private void addFaceDownCardToDeck(PlayerZones playerZones) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        playerZones.getCardsInDeck().addFaceDownCard(cardRepresentation);
    }

    private void addFaceDownCardToDilemmaPile(PlayerZones playerZones) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        playerZones.getCardsInDilemmaPile().addFaceDownCard(cardRepresentation);
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
        for (int i = 0; i < handCount; i++) {
            addFaceDownCardToHand(playerPosition);
        }
    }

    private void addFaceDownCardToHand(PlayerPosition playerPosition) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        cardRenderingSystem.getPlayerCards(playerPosition).getCardsInHand().addFaceDownCard(cardRepresentation);
    }
}
