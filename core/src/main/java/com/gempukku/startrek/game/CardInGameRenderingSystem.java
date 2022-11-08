package com.gempukku.startrek.game;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.core.CardInCoreComponent;
import com.gempukku.startrek.game.hand.CardInHandComponent;
import com.gempukku.startrek.game.layout.CoreLayout;
import com.gempukku.startrek.game.layout.HandLayout;
import com.gempukku.startrek.game.layout.MissionsLayout;
import com.gempukku.startrek.game.layout.PileLayout;
import com.gempukku.startrek.game.mission.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.mission.FaceUpCardInMissionComponent;
import com.gempukku.startrek.game.template.CardTemplates;

public class CardInGameRenderingSystem extends BaseSystem {
    private SpawnSystem spawnSystem;
    private PlayerPositionSystem playerPositionSystem;
    private CameraSystem cameraSystem;
    private TransformSystem transformSystem;
    private CardLookupSystem cardLookupSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private CardStorageSystem cardStorageSystem;

    private final ObjectMap<PlayerPosition, ZonesStatus> playerZonesStatusMap = new ObjectMap<>();

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(CardInHandComponent.class))
                .addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    cardInHandInserted(entities.get(i));
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    cardInHandRemoved(entities.get(i));
                                }
                            }
                        });
        world.getAspectSubscriptionManager().get(Aspect.all(FaceUpCardInMissionComponent.class))
                .addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    faceUpCardInMissionInserted(entities.get(i));
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    faceUpCardInMissionRemoved(entities.get(i));
                                }
                            }
                        });
        world.getAspectSubscriptionManager().get(Aspect.all(FaceDownCardInMissionComponent.class))
                .addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    faceDownCardInMissionInserted(entities.get(i));
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    faceDownCardInMissionRemoved(entities.get(i));
                                }
                            }
                        });
        world.getAspectSubscriptionManager().get(Aspect.all(CardInCoreComponent.class))
                .addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    cardInCoreInserted(entities.get(i));
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; ++i) {
                                    cardInCoreRemoved(entities.get(i));
                                }
                            }
                        });

    }

    private void cardInHandInserted(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String owner = card.getOwner();
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = CardTemplates.createFullCard(cardDefinition, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(entityId);
        cardStorageSystem.getPlayerCards(owner).addCardInHand(cardEntity, cardRepresentation);

        getZonesStatus(owner).setHandDrity(true);
    }

    private void cardInHandRemoved(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String owner = card.getOwner();
        PlayerCards playerCards = cardStorageSystem.getPlayerCards(owner);
        Entity renderedCard = playerCards.removeCardInHand(cardEntity);
        world.deleteEntity(renderedCard);

        getZonesStatus(owner).setHandDrity(true);
    }

    private void cardInCoreInserted(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String owner = card.getOwner();
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        Entity cardRepresentation = CardTemplates.createSmallCard(cardDefinition, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(entityId);
        cardStorageSystem.getPlayerCards(owner).addCardInCore(cardEntity, cardRepresentation);

        getZonesStatus(owner).setCoreDirty(true);
    }

    private void cardInCoreRemoved(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String owner = card.getOwner();
        PlayerCards playerCards = cardStorageSystem.getPlayerCards(owner);
        Entity renderedCard = playerCards.removeCardInCore(cardEntity);
        world.deleteEntity(renderedCard);

        getZonesStatus(owner).setCoreDirty(true);
    }

    private void faceUpCardInMissionInserted(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        FaceUpCardInMissionComponent cardInMission = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
        int missionIndex = cardInMission.getMissionIndex();
        String missionOwner = cardInMission.getMissionOwner();
        addRevealedCardInMission(entityId, cardEntity, missionIndex, missionOwner);
    }

    private void faceUpCardInMissionRemoved(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        FaceUpCardInMissionComponent cardInMission = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
        int missionIndex = cardInMission.getMissionIndex();
        String missionOwner = cardInMission.getMissionOwner();
        removeRevealedCardInMission(cardEntity, missionIndex, missionOwner);
    }

    private void faceDownCardInMissionInserted(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        FaceDownCardInMissionComponent cardInMission = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
        int missionIndex = cardInMission.getMissionIndex();
        String missionOwner = cardInMission.getMissionOwner();
        addRevealedCardInMission(entityId, cardEntity, missionIndex, missionOwner);
    }

    private void faceDownCardInMissionRemoved(int entityId) {
        Entity cardEntity = world.getEntity(entityId);
        FaceDownCardInMissionComponent cardInMission = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
        int missionIndex = cardInMission.getMissionIndex();
        String missionOwner = cardInMission.getMissionOwner();
        removeRevealedCardInMission(cardEntity, missionIndex, missionOwner);
    }

    private Entity addRevealedCardInMission(int entityId, Entity cardEntity, int missionIndex, String missionOwner) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = CardTemplates.createSmallCard(cardDefinition, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(entityId);
        if (cardDefinition.getType() == CardType.Mission) {
            cardStorageSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).addMissionCard(cardEntity, cardRepresentation);
        } else {
            boolean playerMission = missionOwner.equals(card.getOwner());
            if (playerMission) {
                cardStorageSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).addPlayerTopLevelCardInMission(cardEntity, cardRepresentation);
            } else {
                cardStorageSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).addOpponentTopLevelCardInMission(cardEntity, cardRepresentation);
            }
        }

        getZonesStatus(missionOwner).setMissionsDirty(true);

        return cardRepresentation;
    }

    private void removeRevealedCardInMission(Entity cardEntity, int missionIndex, String missionOwner) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        boolean playerMission = missionOwner.equals(card.getOwner());
        MissionCards missionCards = cardStorageSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex);
        if (playerMission) {
            missionCards.removePlayerTopLevelCardInMission(cardEntity);
        } else {
            missionCards.removeOpponentTopLevelCardInMission(cardEntity);
        }
    }

    private ZonesStatus getZonesStatus(String username) {
        PlayerPosition playerPosition = playerPositionSystem.getPlayerPosition(username);
        return getZonesStatus(playerPosition);
    }

    private ZonesStatus getZonesStatus(PlayerPosition playerPosition) {
        ZonesStatus result = playerZonesStatusMap.get(playerPosition);
        if (result == null) {
            result = new ZonesStatus();
            playerZonesStatusMap.put(playerPosition, result);
        }
        return result;
    }

    @Override
    protected void processSystem() {
        setupUnknownPlayersHands();
        setupPlayersDecks();
        setupPlayersDilemmaPiles();

        for (ObjectMap.Entry<PlayerPosition, ZonesStatus> playerZonesStatus : playerZonesStatusMap) {
            PlayerPosition playerPosition = playerZonesStatus.key;
            ZonesStatus zonesStatus = playerZonesStatus.value;
            PlayerCards playerCards = cardStorageSystem.getPlayerCards(playerPosition);
            if (zonesStatus.isHandDrity()) {
                HandLayout.layoutHand(playerCards, playerPosition,
                        cameraSystem.getCamera(), transformSystem);
            }
            if (zonesStatus.isMissionsDirty()) {
                MissionsLayout.layoutMissions(playerCards, playerPosition, transformSystem);
            }
            if (zonesStatus.isCoreDirty()) {
                CoreLayout.layoutCore(playerCards, playerPosition, transformSystem);
            }
            zonesStatus.cleanZones();
        }
    }

    private void setupUnknownPlayersHands() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            if (!username.equals(authenticationHolderSystem.getUsername())) {
                PlayerPosition playerPosition = player.value;
                PlayerCards playerCards = cardStorageSystem.getPlayerCards(playerPosition);
                Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
                layoutPlayerUnknownHand(playerPosition, playerCards, playerEntity);
            }
        }
    }

    private void setupPlayersDecks() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            PlayerPosition playerPosition = player.value;
            Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
            PlayerCards playerCards = cardStorageSystem.getPlayerCards(playerPosition);
            PileLayout.layoutPlayerDeck(playerCards, playerPosition,
                    playerEntity, world, spawnSystem, transformSystem);
        }
    }

    private void setupPlayersDilemmaPiles() {
        for (ObjectMap.Entry<String, PlayerPosition> player : playerPositionSystem.getPlayerPositions()) {
            String username = player.key;
            PlayerPosition playerPosition = player.value;
            Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
            PileLayout.layoutPlayerDilemmaPile(cardStorageSystem.getPlayerCards(playerPosition), playerPosition,
                    playerEntity, world, spawnSystem, transformSystem);
        }
    }

    private void layoutPlayerUnknownHand(PlayerPosition playerPosition, PlayerCards playerCards, Entity playerEntity) {
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        int handCount = publicStats.getHandCount();
        int renderedCount = playerCards.getCardInHandCount();
        if (renderedCount > handCount) {
            int destroyCount = renderedCount - handCount;
            for (int i = 0; i < destroyCount; i++) {
                Entity removedCard = playerCards.removeOneCardInHand();
                world.deleteEntity(removedCard);
            }
            getZonesStatus(playerPosition).setHandDrity(true);
        } else if (renderedCount < handCount) {
            int createCount = handCount - renderedCount;
            for (int i = 0; i < createCount; i++) {
                addFaceDownCardToHand(playerPosition);
            }
            getZonesStatus(playerPosition).setHandDrity(true);
        }
    }

    private Entity addFaceDownCardToHand(PlayerPosition playerPosition) {
        Entity cardRepresentation = CardTemplates.createFaceDownCard(spawnSystem);
        cardStorageSystem.getPlayerCards(playerPosition).addCardInHand(null, cardRepresentation);
        return cardRepresentation;
    }
}
