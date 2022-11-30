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
import com.gempukku.startrek.game.card.SpecialActionLookupSystem;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.zone.RenderedCardGroup;
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

        Entity cardRepresentation = CardTemplates.createRenderedCard(cardDefinition, CardZone.Hand, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());
        moveCardToHand(cardEntity, cardRepresentation, card, cardRenderingSystem);
    }

    public static void moveCardToHand(Entity cardEntity, Entity cardRepresentation, CardComponent card,
                                      CardRenderingSystem cardRenderingSystem) {
        String owner = card.getOwner();
        cardRenderingSystem.getPlayerCards(owner).getCardsInHand().addFaceUpCard(cardEntity, cardRepresentation);
    }

    public static void addCardInCore(Entity cardEntity, CardComponent card,
                                     CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                     CardRenderingSystem cardRenderingSystem) {
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        Entity cardRepresentation = CardTemplates.createRenderedCard(cardDefinition, CardZone.Core, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());
        moveCardToCore(cardEntity, cardRepresentation, card, cardRenderingSystem);
    }

    public static void moveCardToCore(Entity cardEntity, Entity cardRepresentation, CardComponent card,
                                      CardRenderingSystem cardRenderingSystem) {
        String owner = card.getOwner();
        cardRenderingSystem.getPlayerCards(owner).getCardsInCore().addFaceUpCard(cardEntity, cardRepresentation);
    }

    public static void addCardInBrig(Entity cardEntity, CardComponent card,
                                     CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                     CardRenderingSystem cardRenderingSystem) {
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        Entity cardRepresentation = CardTemplates.createRenderedCard(cardDefinition, CardZone.Brig, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());
        moveCardToBrig(cardEntity, cardRepresentation, cardRenderingSystem);
    }

    public static void moveCardToBrig(Entity cardEntity,
                                      Entity cardRepresentation, CardRenderingSystem cardRenderingSystem) {
        CardInBrigComponent cardInBrig = cardEntity.getComponent(CardInBrigComponent.class);
        cardRenderingSystem.getPlayerCards(cardInBrig.getBrigOwner()).getCardsInBrig().addFaceUpCard(cardEntity, cardRepresentation);
    }

    public static void addCardOnStack(Entity objectEntity, CardComponent card,
                                      CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                      CardRenderingSystem cardRenderingSystem,
                                      ComponentMapper<OrderComponent> orderComponentMapper) {
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        ObjectOnStackComponent objectOnStack = objectEntity.getComponent(ObjectOnStackComponent.class);
        Entity objectRepresentation = CardTemplates.createRenderedCard(cardDefinition, CardZone.Stack, spawnSystem);
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

    public static void addSpecialActionOnStack(Entity objectEntity, String specialAction,
                                               SpecialActionLookupSystem specialActionLookupSystem, SpawnSystem spawnSystem,
                                               CardRenderingSystem cardRenderingSystem,
                                               ComponentMapper<OrderComponent> orderComponentMapper) {
        ObjectOnStackComponent objectOnStack = objectEntity.getComponent(ObjectOnStackComponent.class);
        Entity objectRepresentation = CardTemplates.createSpecialAction(specialAction, specialActionLookupSystem, spawnSystem);
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
        Entity renderedCard = CardTemplates.createRenderedCard(cardDefinition, CardZone.DiscardPile, spawnSystem);
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
        CardType attachedToCardType = cardLookupSystem.getCardDefinition(attachedToCardEntity).getType();

        CardInMissionComponent cardInMission = attachedToCardEntity.getComponent(CardInMissionComponent.class);

        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = CardTemplates.createRenderedCard(cardDefinition, CardZone.Mission, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());

        cardRenderingSystem.addFaceUpAttachedCard(attachedToCardEntity, cardEntity, cardRepresentation);
    }

    public static Entity addCardInMission(Entity cardEntity, String missionOwner, int missionIndex,
                                          CardLookupSystem cardLookupSystem, SpawnSystem spawnSystem,
                                          CardRenderingSystem cardRenderingSystem) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        String cardId = card.getCardId();
        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);

        Entity cardRepresentation = CardTemplates.createRenderedCard(cardDefinition, CardZone.Mission, spawnSystem);
        cardRepresentation.getComponent(ServerCardReferenceComponent.class).setEntityId(cardEntity.getId());
        moveCardToMission(cardEntity, missionOwner, missionIndex, cardRepresentation, card, cardDefinition, cardRenderingSystem);

        return cardRepresentation;
    }

    public static RenderedCardGroup getCardGroupForCardInMission(
            CardType cardType, String cardOwner,
            String missionOwner, int missionIndex,
            CardRenderingSystem cardRenderingSystem) {
        if (cardType == CardType.Mission)
            return cardRenderingSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).getMissionCards();
        if (missionOwner.equals(cardOwner))
            return cardRenderingSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).getMissionOwnerCards();
        return cardRenderingSystem.getPlayerCards(missionOwner).getMissionCards(missionIndex).getOpposingCards();
    }

    public static void moveCardToMission(Entity cardEntity, String missionOwner, int missionIndex, Entity cardRepresentation,
                                         CardComponent card, CardDefinition cardDefinition, CardRenderingSystem cardRenderingSystem) {
        getCardGroupForCardInMission(cardDefinition.getType(), card.getOwner(), missionOwner, missionIndex, cardRenderingSystem).
                addFaceUpCard(cardEntity, cardRepresentation);
    }
}
