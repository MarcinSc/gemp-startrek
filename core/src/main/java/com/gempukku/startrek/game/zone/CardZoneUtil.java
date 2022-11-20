package com.gempukku.startrek.game.zone;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.common.OrderComponent;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.template.CardTemplates;

public class CardZoneUtil {
    public static void addCardInHand(Entity cardEntity, CardComponent card,
                                     CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                     CardRenderingSystem cardRenderingSystem) {
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = CardTemplates.createFullCard(cardDefinition, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());
        moveCardToHand(cardEntity, cardRepresentation, card, cardRenderingSystem);
    }

    public static void moveCardToHand(Entity cardEntity, Entity cardRepresentation, CardComponent card,
                                      CardRenderingSystem cardRenderingSystem) {
        String owner = card.getOwner();
        cardRenderingSystem.getPlayerCards(owner).addCardInHand(cardEntity, cardRepresentation);
    }

    public static void addCardInCore(Entity cardEntity, CardComponent card,
                                     CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                     CardRenderingSystem cardRenderingSystem) {
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        Entity cardRepresentation = CardTemplates.createSmallCard(cardDefinition, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());
        moveCardToCore(cardEntity, cardRepresentation, card, cardRenderingSystem);
    }

    public static void moveCardToCore(Entity cardEntity, Entity cardRepresentation, CardComponent card,
                                      CardRenderingSystem cardRenderingSystem) {
        String owner = card.getOwner();
        cardRenderingSystem.getPlayerCards(owner).addCardInCore(cardEntity, cardRepresentation);
    }

    public static void addCardInBrig(Entity cardEntity, CardComponent card,
                                     CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                     CardRenderingSystem cardRenderingSystem) {
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        Entity cardRepresentation = CardTemplates.createSmallCard(cardDefinition, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());
        moveCardToBrig(cardEntity, cardRepresentation, cardRenderingSystem);
    }

    public static void moveCardToBrig(Entity cardEntity,
                                      Entity cardRepresentation, CardRenderingSystem cardRenderingSystem) {
        CardInBrigComponent cardInBrig = cardEntity.getComponent(CardInBrigComponent.class);
        cardRenderingSystem.getPlayerCards(cardInBrig.getBrigOwner()).addCardInBrig(cardEntity, cardRepresentation);
    }

    public static void addObjectOnStack(Entity objectEntity, CardComponent card,
                                        CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                        CardRenderingSystem cardRenderingSystem,
                                        ComponentMapper<OrderComponent> orderComponentMapper) {
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        ObjectOnStackComponent objectOnStack = objectEntity.getComponent(ObjectOnStackComponent.class);
        Entity objectRepresentation = CardTemplates.createFullCard(cardDefinition, spawnSystem);
        OrderComponent order = orderComponentMapper.create(objectRepresentation);
        order.setValue(objectOnStack.getStackIndex());
        objectRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(objectEntity.getId());
        moveObjectToStack(objectEntity, objectRepresentation, cardRenderingSystem);
    }

    public static void moveObjectToStack(Entity objectEntity, Entity objectRepresentation, CardRenderingSystem cardRenderingSystem) {
        cardRenderingSystem.getCommonZones().addObjectToStack(objectEntity, objectRepresentation);
    }

    public static void addFaceUpCardInMission(Entity cardEntity,
                                              CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                              CardRenderingSystem cardRenderingSystem) {
        FaceUpCardInMissionComponent cardInMission = cardEntity.getComponent(FaceUpCardInMissionComponent.class);
        int missionIndex = cardInMission.getMissionIndex();
        String missionOwner = cardInMission.getMissionOwner();
        addRevealedCardInMission(cardEntity, missionIndex, missionOwner,
                cardLookupSystem, spawnSystem, cardRenderingSystem);
    }

    public static void addFaceDownCardInMission(Entity cardEntity,
                                                CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                                CardRenderingSystem cardRenderingSystem) {
        FaceDownCardInMissionComponent cardInMission = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
        int missionIndex = cardInMission.getMissionIndex();
        String missionOwner = cardInMission.getMissionOwner();
        addRevealedCardInMission(cardEntity, missionIndex, missionOwner,
                cardLookupSystem, spawnSystem, cardRenderingSystem);
    }

    private static Entity addRevealedCardInMission(Entity cardEntity, int missionIndex, String missionOwner,
                                                   CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                                   CardRenderingSystem cardRenderingSystem) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = CardTemplates.createSmallCard(cardDefinition, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());
        moveCardToMission(cardEntity, missionOwner, missionIndex, cardRepresentation, card, cardDefinition, cardRenderingSystem);

        return cardRepresentation;
    }

    public static void moveCardToMission(Entity cardEntity, String missionOwner, int missionIndex, Entity cardRepresentation,
                                         CardComponent card, CardDefinition cardDefinition, CardRenderingSystem cardRenderingSystem) {
        if (cardDefinition.getType() == CardType.Mission) {
            cardRenderingSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).addMissionCard(cardEntity, cardRepresentation);
        } else {
            boolean playerMission = missionOwner.equals(card.getOwner());
            if (playerMission) {
                cardRenderingSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).addPlayerTopLevelCardInMission(cardEntity, cardRepresentation);
            } else {
                cardRenderingSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).addOpponentTopLevelCardInMission(cardEntity, cardRepresentation);
            }
        }
    }
}
