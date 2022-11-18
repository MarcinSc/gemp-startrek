package com.gempukku.startrek.game.render;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.game.PlayerPosition;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.layout.*;
import com.gempukku.startrek.game.render.zone.CommonZones;
import com.gempukku.startrek.game.render.zone.PlayerZones;
import com.gempukku.startrek.game.template.CardTemplates;
import com.gempukku.startrek.game.zone.CardZone;

public class CardRenderingSystem extends BaseSystem {
    private PlayerPositionSystem playerPositionSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private SpawnSystem spawnSystem;
    private TransformSystem transformSystem;
    private CameraSystem cameraSystem;

    private final ObjectMap<PlayerPosition, PlayerZones> playerCardsMap = new ObjectMap<>();
    private final CommonZones commonZones = new CommonZones();

    public PlayerZones getPlayerCards(String username) {
        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        return getPlayerCards(playerPosition);
    }

    public PlayerZones getPlayerCards(PlayerPosition playerPosition) {
        PlayerZones playerZones = playerCardsMap.get(playerPosition);
        if (playerZones == null) {
            playerZones = new PlayerZones();
            playerCardsMap.put(playerPosition, playerZones);
        }
        return playerZones;
    }

    public Entity removeRenderedCard(Entity cardEntity, CardZone oldZone) {
        for (PlayerZones playerZones : playerCardsMap.values()) {
            Entity renderedCard = playerZones.removeCard(cardEntity, oldZone);
            if (renderedCard != null)
                return renderedCard;
        }
        Entity renderedCard = commonZones.removeObject(cardEntity, oldZone);
        if (renderedCard != null)
            return renderedCard;
        return null;
    }

    public Entity findRenderedCard(Entity cardEntity) {
        for (PlayerZones playerZones : playerCardsMap.values()) {
            Entity renderedCard = playerZones.findRenderedCard(cardEntity);
            if (renderedCard != null)
                return renderedCard;
        }
        Entity renderedCard = commonZones.findRenderedCard(cardEntity);
        if (renderedCard != null)
            return renderedCard;
        return null;
    }

    public CommonZones getCommonZones() {
        return commonZones;
    }

    @Override
    protected void processSystem() {
        setupUnknownPlayersHands();
        setupPlayersDecks();
        setupPlayersDilemmaPiles();

        if (commonZones.isStackDirty()) {
            StackLayout.layoutStack(commonZones, cameraSystem.getCamera(), transformSystem);
            commonZones.cleanup();
        }

        for (ObjectMap.Entry<PlayerPosition, PlayerZones> playerZonesStatus : playerCardsMap) {
            PlayerPosition playerPosition = playerZonesStatus.key;
            PlayerZones playerZones = playerZonesStatus.value;
            if (playerZones.isHandDirty()) {
                HandLayout.layoutHand(playerZones, playerPosition,
                        cameraSystem.getCamera(), transformSystem);
            }
            for (int i = 0; i < 5; i++) {
                if (playerZones.isMissionDirty(i)) {
                    MissionsLayout.layoutMission(playerZones, i, playerPosition, transformSystem);
                }
            }
            if (playerZones.isCoreDirty()) {
                CoreLayout.layoutCore(playerZones, playerPosition, transformSystem);
            }
            playerZones.cleanup();
        }
    }

    private void setupUnknownPlayersHands() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            if (!username.equals(authenticationHolderSystem.getUsername())) {
                PlayerPosition playerPosition = player.value;
                PlayerZones playerZones = getPlayerCards(playerPosition);
                Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
                layoutPlayerUnknownHand(playerPosition, playerZones, playerEntity);
            }
        }
    }

    private void setupPlayersDecks() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            PlayerPosition playerPosition = player.value;
            Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
            PlayerZones playerZones = getPlayerCards(playerPosition);
            PileLayout.layoutPlayerDeck(playerZones, playerPosition,
                    playerEntity, world, spawnSystem, transformSystem);
        }
    }

    private void setupPlayersDilemmaPiles() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            PlayerPosition playerPosition = player.value;
            Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
            PileLayout.layoutPlayerDilemmaPile(getPlayerCards(playerPosition), playerPosition,
                    playerEntity, world, spawnSystem, transformSystem);
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

    private Entity addFaceDownCardToHand(PlayerPosition playerPosition) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        getPlayerCards(playerPosition).addCardInHand(null, cardRepresentation);
        return cardRepresentation;
    }
}
