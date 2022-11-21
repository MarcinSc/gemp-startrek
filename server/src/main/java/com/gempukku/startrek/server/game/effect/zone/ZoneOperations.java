package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.game.PlayerDiscardPileComponent;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.*;
import com.gempukku.startrek.server.game.deck.PlayerDeckComponent;
import com.gempukku.startrek.server.game.deck.PlayerDilemmaPileComponent;
import com.gempukku.startrek.server.game.stack.ObjectStackSystem;

public class ZoneOperations extends BaseSystem {
    private EventSystem eventSystem;
    private PlayerResolverSystem playerResolverSystem;
    private ObjectStackSystem objectStackSystem;

    private ComponentMapper<CardInPlayComponent> cardInPlayComponentMapper;
    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<FaceDownCardInMissionComponent> faceDownCardInMissionComponentMapper;
    private ComponentMapper<CardInMissionComponent> cardInMissionComponentMapper;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private ComponentMapper<CardInBrigComponent> cardInBrigComponentMapper;
    private ComponentMapper<CardInCoreComponent> cardInCoreComponentMapper;
    private ComponentMapper<CardInDiscardComponent> cardInDiscardComponentMapper;
    private ComponentMapper<CardInDilemmaPileComponent> cardInDilemmaPileComponentMapper;
    private ComponentMapper<ObjectOnStackComponent> cardOnStackComponentMapper;

    public void moveCardToHand(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Hand;
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        card.setCardZone(newZone);
        CardInHandComponent cardInHand = cardInHandComponentMapper.create(cardEntity);
        cardInHand.setOwner(card.getOwner());
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setHandCount(stats.getHandCount() + 1);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    private void notifyZoneChange(Entity cardEntity, CardZone lastZone, CardZone newZone) {
        if (lastZone != null && lastZone != newZone) {
            eventSystem.fireEvent(new CardChangedZones(lastZone), cardEntity);
        }
    }

    public void removeCardFromHand(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        cardInHandComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setHandCount(stats.getHandCount() - 1);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    public void moveEffectToStack(Entity effectEntity, int abilityIndex) {
        ObjectOnStackComponent cardOnStack = cardOnStackComponentMapper.create(effectEntity);
        cardOnStack.setType("effect");
        cardOnStack.setAbilityIndex(abilityIndex);
        objectStackSystem.stackEntity(effectEntity);
        eventSystem.fireEvent(EntityUpdated.instance, effectEntity);
    }

    public void removeEffectFromStack() {
        Entity effectEntity = objectStackSystem.removeTopMostFromStack();
        world.deleteEntity(effectEntity);
    }

    public void moveCardToStack(Entity cardEntity, int abilityIndex) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Stack;
        card.setCardZone(newZone);
        ObjectOnStackComponent cardOnStack = cardOnStackComponentMapper.create(cardEntity);
        cardOnStack.setType("card");
        cardOnStack.setAbilityIndex(abilityIndex);
        objectStackSystem.stackEntity(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    public void removeCardFromStack(Entity cardEntity) {
        cardOnStackComponentMapper.remove(cardEntity);
        objectStackSystem.removeFromStack(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveCardToBrig(Entity cardEntity, Entity brigPlayerEntity) {
        cardInPlayComponentMapper.create(cardEntity);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Brig;
        card.setCardZone(newZone);
        GamePlayerComponent gamePlayer = brigPlayerEntity.getComponent(GamePlayerComponent.class);
        CardInBrigComponent cardInBrig = cardInBrigComponentMapper.create(cardEntity);
        cardInBrig.setBrigOwner(gamePlayer.getName());
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    public void removeCardFromBrig(Entity cardEntity) {
        cardInBrigComponentMapper.remove(cardEntity);
        cardInPlayComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveCardToMission(Entity cardEntity, Entity missionEntity, boolean faceUp) {
        cardInPlayComponentMapper.create(cardEntity);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Mission;
        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
        card.setCardZone(newZone);
        CardInMissionComponent cardInMission = cardInMissionComponentMapper.create(cardEntity);
        cardInMission.setMissionOwner(mission.getOwner());
        cardInMission.setMissionIndex(mission.getMissionIndex());
        if (faceUp) {
            faceUpCardInMissionComponentMapper.create(cardEntity);
        } else {
            FaceDownCardInMissionComponent faceDownCard = faceDownCardInMissionComponentMapper.create(cardEntity);
            faceDownCard.setOwner(card.getOwner());
            ObjectMap<String, Integer> playerFaceDownCardsCount = mission.getPlayerFaceDownCardsCount();
            int oldCount = playerFaceDownCardsCount.get(card.getOwner(), 0);
            playerFaceDownCardsCount.put(card.getOwner(), oldCount + 1);
            eventSystem.fireEvent(EntityUpdated.instance, missionEntity);
        }
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    public void removeCardFromMission(Entity cardEntity) {
        cardInPlayComponentMapper.remove(cardEntity);
        CardInMissionComponent cardInMission = cardInMissionComponentMapper.get(cardEntity);
        FaceDownCardInMissionComponent faceDownInMission = faceDownCardInMissionComponentMapper.get(cardEntity);
        if (faceDownInMission != null) {
            int missionIndex = cardInMission.getMissionIndex();
            String missionOwner = cardInMission.getMissionOwner();
            Entity playerEntity = playerResolverSystem.findPlayerEntity(missionOwner);
            Entity missionEntity = MissionOperations.findMission(world, playerEntity, missionIndex);
            faceDownCardInMissionComponentMapper.remove(cardEntity);

            CardComponent card = cardEntity.getComponent(CardComponent.class);
            MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
            ObjectMap<String, Integer> playerFaceDownCardsCount = mission.getPlayerFaceDownCardsCount();
            int oldCount = playerFaceDownCardsCount.get(card.getOwner(), 0);
            playerFaceDownCardsCount.put(card.getOwner(), oldCount - 1);
            eventSystem.fireEvent(EntityUpdated.instance, missionEntity);
        }
        faceUpCardInMissionComponentMapper.remove(cardEntity);
        cardInMissionComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveCardToCore(Entity cardEntity) {
        cardInPlayComponentMapper.create(cardEntity);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Core;
        card.setCardZone(newZone);
        cardInCoreComponentMapper.create(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    public void removeCardFromCore(Entity cardEntity) {
        cardInCoreComponentMapper.remove(cardEntity);
        cardInPlayComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveCardToBottomOfDeck(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Deck;
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        deck.getCards().insert(0, cardEntity.getId());
        card.setCardZone(newZone);

        stats.setDeckCount(stats.getDeckCount() + 1);

        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    public void moveCardToTopOfDeck(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Deck;
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        deck.getCards().add(cardEntity.getId());
        card.setCardZone(newZone);

        stats.setDeckCount(stats.getDeckCount() + 1);

        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    public Entity removeTopCardOfDeck(Entity playerEntity) {
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        Array<Integer> cards = deck.getCards();
        if (cards.size == 0)
            return null;

        Entity cardEntity = world.getEntity(cards.removeIndex(cards.size - 1));

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDeckCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        return cardEntity;
    }

    public void removeCardFromDeck(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        Array<Integer> cards = deck.getCards();
        cards.removeValue(cardEntity.getId(), false);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDeckCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    public void moveCardToDiscardPile(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.DiscardPile;
        card.setCardZone(newZone);
        cardInDiscardComponentMapper.create(cardEntity);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDiscardPileComponent discard = playerEntity.getComponent(PlayerDiscardPileComponent.class);
        Array<Integer> cards = discard.getCards();
        cards.add(cardEntity.getId());
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    public void removeCardFromDiscardPile(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        cardInDiscardComponentMapper.remove(cardEntity);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDiscardPileComponent discard = playerEntity.getComponent(PlayerDiscardPileComponent.class);
        Array<Integer> cards = discard.getCards();
        cards.removeValue(cardEntity.getId(), false);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    public void moveCardToBottomOfDilemmaPile(Entity cardEntity, boolean faceUp) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.DilemmaPile;
        card.setCardZone(newZone);
        CardInDilemmaPileComponent cardInDilemmaPile = cardInDilemmaPileComponentMapper.create(cardEntity);
        cardInDilemmaPile.setFaceUp(faceUp);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        Array<Integer> cards = dilemmaPile.getCards();
        cards.insert(0, cardEntity.getId());
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDilemmaCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    public void moveCardToTopOfDilemmaPile(Entity cardEntity, boolean faceUp) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.DilemmaPile;
        card.setCardZone(newZone);
        CardInDilemmaPileComponent cardInDilemmaPile = cardInDilemmaPileComponentMapper.create(cardEntity);
        cardInDilemmaPile.setFaceUp(faceUp);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        Array<Integer> cards = dilemmaPile.getCards();
        cards.add(cardEntity.getId());
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDilemmaCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        notifyZoneChange(cardEntity, oldZone, newZone);
    }

    public Entity removeTopCardFromDilemmaPile(Entity playerEntity) {
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        Array<Integer> cards = dilemmaPile.getCards();
        if (cards.size == 0)
            return null;

        Entity cardEntity = world.getEntity(cards.removeIndex(cards.size - 1));
        cardInDilemmaPileComponentMapper.remove(cardEntity);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDilemmaCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        return cardEntity;
    }

    public void removeCardFromDilemmaPile(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        Array<Integer> cards = dilemmaPile.getCards();
        cards.removeValue(cardEntity.getId(), false);
        cardInDilemmaPileComponentMapper.remove(cardEntity);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDilemmaCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveToNewZone(Entity cardEntity, CardZone zone) {
        switch (zone) {
            case Hand:
                moveCardToHand(cardEntity);
                break;
            case Core:
                moveCardToCore(cardEntity);
                break;
            case DiscardPile:
                moveCardToDiscardPile(cardEntity);
                break;
            default:
                throw new GdxRuntimeException("Can't blindly move a card to zone: " + zone);
        }
    }

    public void removeFromCurrentZone(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone zone = card.getCardZone();
        switch (zone) {
            case Hand:
                removeCardFromHand(cardEntity);
                break;
            case Core:
                removeCardFromCore(cardEntity);
                break;
            case Stack:
                removeCardFromStack(cardEntity);
                break;
            case Brig:
                removeCardFromBrig(cardEntity);
                break;
            case Mission:
                removeCardFromMission(cardEntity);
                break;
            case Deck:
                removeCardFromDeck(cardEntity);
                break;
            case DiscardPile:
                removeCardFromDiscardPile(cardEntity);
                break;
            case DilemmaPile:
                removeCardFromDilemmaPile(cardEntity);
                break;
        }
    }

    @Override
    protected void processSystem() {

    }
}
