package com.gempukku.startrek.game.zone;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.common.OrderComponent;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.EffectComponent;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.template.CardTemplates;

public class CardZoneUtil {
    public static boolean isCardRendered(CardZone zone) {
        if (zone == CardZone.Deck || zone == CardZone.DilemmaPile || zone == CardZone.DilemmaStack)
            return false;
        return true;
    }

    public static boolean isBigCard(CardZone zone) {
        if (zone == CardZone.Hand || zone == CardZone.Stack || zone == CardZone.DiscardPile)
            return true;
        return false;
    }

    public static boolean isCardFaceUp(CardZone zone, CardType cardType, boolean owner) {
        if (zone == CardZone.Brig || zone == CardZone.Core || zone == CardZone.Stack || zone == CardZone.DiscardPile) {
            return true;
        }
        if (owner && (zone == CardZone.Hand || zone == CardZone.Mission)) {
            return true;
        }
        if (zone == CardZone.Mission && (cardType == CardType.Ship || cardType == CardType.Mission))
            return true;
        return false;
    }

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

    public static void addCardOnStack(Entity objectEntity, CardComponent card,
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

    public static void addEffectOnStack(Entity objectEntity, EffectComponent effect,
                                        CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                        CardRenderingSystem cardRenderingSystem,
                                        ComponentMapper<OrderComponent> orderComponentMapper) {
        String cardId = effect.getSourceCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        ObjectOnStackComponent objectOnStack = objectEntity.getComponent(ObjectOnStackComponent.class);
        Entity objectRepresentation = CardTemplates.createEffect(cardDefinition, objectOnStack.getAbilityIndex(), spawnSystem);
        OrderComponent order = orderComponentMapper.create(objectRepresentation);
        order.setValue(objectOnStack.getStackIndex());
        objectRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(objectEntity.getId());
        moveObjectToStack(objectEntity, objectRepresentation, cardRenderingSystem);
    }

    public static void moveObjectToStack(Entity objectEntity, Entity objectRepresentation, CardRenderingSystem cardRenderingSystem) {
        cardRenderingSystem.getCommonZones().addObjectToStack(objectEntity, objectRepresentation);
    }

    public static Entity setTopDiscardPileCard(Entity cardEntity, CardComponent card, CardLookupSystem cardLookupSystem,
                                               SpawnSystem spawnSystem, CardRenderingSystem cardRenderingSystem) {
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardEntity);
        Entity renderedCard = CardTemplates.createFullCard(cardDefinition, spawnSystem);
        return cardRenderingSystem.getPlayerCards(card.getOwner()).setTopDiscardPileCard(cardEntity, renderedCard);
    }

    public static Entity moveCardAsTopDiscardPileCard(Entity cardEntity, Entity renderedCard, CardRenderingSystem cardRenderingSystem) {
        String owner = cardEntity.getComponent(CardComponent.class).getOwner();
        return cardRenderingSystem.getPlayerCards(owner).setTopDiscardPileCard(cardEntity, renderedCard);
    }

    public static void addAttachedCardInMission(
            Entity cardEntity, Entity attachedToCardEntity,
            CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
            CardRenderingSystem cardRenderingSystem) {
        CardInMissionComponent cardInMission = cardEntity.getComponent(CardInMissionComponent.class);
        int missionIndex = cardInMission.getMissionIndex();
        String missionOwner = cardInMission.getMissionOwner();

        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = CardTemplates.createSmallCard(cardDefinition, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());

        cardRenderingSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).
                addAttachedCard(attachedToCardEntity, cardEntity, cardRepresentation);
    }

    public static Entity addCardInMission(Entity cardEntity, String missionOwner, int missionIndex,
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
