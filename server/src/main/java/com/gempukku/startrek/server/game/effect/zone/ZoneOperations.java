package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.game.PlayerDiscardPileComponent;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.*;
import com.gempukku.startrek.server.game.deck.PlayerDeckComponent;
import com.gempukku.startrek.server.game.deck.PlayerDilemmaPileComponent;

public class ZoneOperations extends BaseSystem {
    private EventSystem eventSystem;
    private PlayerResolverSystem playerResolverSystem;

    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<FaceDownCardInMissionComponent> faceDownCardInMissionComponentMapper;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private ComponentMapper<CardInBrigComponent> cardInBrigComponentMapper;
    private ComponentMapper<CardInCoreComponent> cardInCoreComponentMapper;
    private ComponentMapper<CardInDiscardComponent> cardInDiscardComponentMapper;
    private ComponentMapper<CardInDilemmaPileComponent> cardInDilemmaPileComponentMapper;
    private ComponentMapper<CardOnStackComponent> cardOnStackComponentMapper;

    public void moveCardToHand(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        card.setCardZone(CardZone.Hand);
        CardInHandComponent cardInHand = cardInHandComponentMapper.create(cardEntity);
        cardInHand.setOwner(card.getOwner());
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setHandCount(stats.getHandCount() + 1);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
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

    public void moveCardToStack(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        card.setCardZone(CardZone.Stack);
        cardOnStackComponentMapper.create(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void removeCardFromStack(Entity cardEntity) {
        cardOnStackComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveCardToBrig(Entity cardEntity, Entity brigPlayerEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        card.setCardZone(CardZone.Brig);
        GamePlayerComponent gamePlayer = brigPlayerEntity.getComponent(GamePlayerComponent.class);
        CardInBrigComponent cardInBrig = cardInBrigComponentMapper.create(cardEntity);
        cardInBrig.setBrigOwner(gamePlayer.getName());
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void removeCardFromBrig(Entity cardEntity) {
        cardInBrigComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveCardToMission(Entity cardEntity, Entity missionEntity, boolean faceUp) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
        card.setCardZone(CardZone.Mission);
        if (faceUp) {
            FaceUpCardInMissionComponent cardInMission = faceUpCardInMissionComponentMapper.create(cardEntity);
            cardInMission.setMissionOwner(mission.getOwner());
            cardInMission.setMissionIndex(mission.getMissionIndex());
        } else {
            FaceDownCardInMissionComponent cardInMission = faceDownCardInMissionComponentMapper.create(cardEntity);
            cardInMission.setOwner(card.getOwner());
            cardInMission.setMissionOwner(mission.getOwner());
            cardInMission.setMissionIndex(mission.getMissionIndex());
        }
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void removeCardFromMission(Entity cardEntity) {
        faceDownCardInMissionComponentMapper.remove(cardEntity);
        faceUpCardInMissionComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveCardToCore(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        card.setCardZone(CardZone.Core);
        cardInCoreComponentMapper.create(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void removeCardFromCore(Entity cardEntity) {
        cardInCoreComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveCardToBottomOfDeck(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        deck.getCards().insert(0, cardEntity.getId());
        card.setCardZone(CardZone.Deck);

        stats.setDeckCount(stats.getDeckCount() + 1);

        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    public void moveCardToTopOfDeck(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        deck.getCards().add(cardEntity.getId());
        card.setCardZone(CardZone.Deck);

        stats.setDeckCount(stats.getDeckCount() + 1);

        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
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
        card.setCardZone(CardZone.DiscardPile);
        cardInDiscardComponentMapper.create(cardEntity);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDiscardPileComponent discard = playerEntity.getComponent(PlayerDiscardPileComponent.class);
        Array<Integer> cards = discard.getCards();
        cards.add(cardEntity.getId());
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
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
        card.setCardZone(CardZone.DilemmaPile);
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
    }

    public void moveCardToTopOfDilemmaPile(Entity cardEntity, boolean faceUp) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        card.setCardZone(CardZone.DilemmaPile);
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
            case Stack:
                moveCardToStack(cardEntity);
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
