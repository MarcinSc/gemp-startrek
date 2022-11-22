package com.gempukku.startrek.game.render.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.game.zone.CardZone;

public class PlayerZones {
    private final PlayerZonesStatus playerZonesStatus = new PlayerZonesStatus();

    private final ObjectMap<Entity, Entity> cardToRenderedMap = new ObjectMap<>();
    private final Array<Entity> cardsInHand = new Array<>();
    private final Array<Entity> cardsInDeck = new Array<>();
    private final Array<Entity> cardsInDilemmaPile = new Array<>();
    private final Array<Entity> cardsInCore = new Array<>();
    private final Array<Entity> cardsInBrig = new Array<>();
    private Entity topDiscardPileCard;

    private final Array<MissionCards> missionCards = new Array<>();

    public PlayerZones() {
        for (int i = 0; i < 5; i++) {
            missionCards.add(new MissionCards());
        }
    }

    public Entity setTopDiscardPileCard(Entity card, Entity renderedCard) {
        Entity oldTopDiscardPileCard = topDiscardPileCard;
        cardToRenderedMap.put(card, renderedCard);
        topDiscardPileCard = renderedCard;
        playerZonesStatus.setDiscardPileDirty();
        return oldTopDiscardPileCard;
    }

    public Entity getTopDiscardPileCard() {
        return topDiscardPileCard;
    }

    public void addCardInCore(Entity card, Entity renderedCard) {
        cardToRenderedMap.put(card, renderedCard);
        cardsInCore.add(renderedCard);
        playerZonesStatus.setCoreDirty();
    }

    public void addCardInBrig(Entity card, Entity renderedCard) {
        cardToRenderedMap.put(card, renderedCard);
        cardsInBrig.add(renderedCard);
        playerZonesStatus.setBrigDirty();
    }

    public void addCardInHand(Entity card, Entity renderedCard) {
        if (card != null) {
            cardToRenderedMap.put(card, renderedCard);
        }
        cardsInHand.add(renderedCard);
        playerZonesStatus.setHandDrity();
    }

    public void addCardInDeck(Entity renderedCard) {
        cardsInDeck.add(renderedCard);
        playerZonesStatus.setDeckDirty();
    }

    public void addCardInDilemmaPile(Entity renderedCard) {
        cardsInDilemmaPile.add(renderedCard);
        playerZonesStatus.setDilemmaPileDirty();
    }

    public MissionCards getMissionCards(int missionIndex) {
        return missionCards.get(missionIndex);
    }

    public Entity removeOneCardInHand() {
        Entity renderedCard = removeLast(cardsInHand);
        playerZonesStatus.setHandDrity();
        return renderedCard;
    }

    public Entity removeCardInHand(Entity card) {
        Entity renderedCard = cardToRenderedMap.remove(card);
        cardsInHand.removeValue(renderedCard, true);
        playerZonesStatus.setHandDrity();
        return renderedCard;
    }

    public Entity removeOneCardInDeck() {
        Entity renderedCard = removeLast(cardsInDeck);
        playerZonesStatus.setDeckDirty();
        return renderedCard;
    }

    public Entity removeOneCardInDilemmaPile() {
        Entity renderedCard = removeLast(cardsInDilemmaPile);
        playerZonesStatus.setDilemmaPileDirty();
        return renderedCard;
    }

    public Entity removeCardInCore(Entity card) {
        Entity renderedCard = cardToRenderedMap.remove(card);
        cardsInCore.removeValue(renderedCard, true);
        playerZonesStatus.setCoreDirty();
        return renderedCard;
    }

    public Entity removeCardInBrig(Entity card) {
        Entity renderedCard = cardToRenderedMap.remove(card);
        cardsInBrig.removeValue(renderedCard, true);
        playerZonesStatus.setBrigDirty();
        return renderedCard;
    }

    private Entity removeLast(Array<Entity> deck) {
        return deck.removeIndex(deck.size - 1);
    }

    public int getCardInHandCount() {
        return cardsInHand.size;
    }

    public int getCardInDeckCount() {
        return cardsInDeck.size;
    }

    public int getCardInDilemmaCount() {
        return cardsInDilemmaPile.size;
    }

    public Array<Entity> getCardsInHand() {
        return cardsInHand;
    }

    public Array<Entity> getCardsInDeck() {
        return cardsInDeck;
    }

    public Array<Entity> getCardsInDilemmaPile() {
        return cardsInDilemmaPile;
    }

    public Array<Entity> getCardsInCore() {
        return cardsInCore;
    }

    public Entity findRenderedCard(Entity card) {
        Entity result = cardToRenderedMap.get(card);
        if (result == null) {
            for (MissionCards missionCard : missionCards) {
                result = missionCard.findRenderedCard(card);
                if (result != null)
                    return result;
            }
        }
        return result;
    }

    public boolean isMissionDirty(int missionIndex) {
        return missionCards.get(missionIndex).isMissionDirty();
    }

    public boolean isBrigDirty() {
        return playerZonesStatus.isBrigDirty();
    }

    public boolean isCoreDirty() {
        return playerZonesStatus.isCoreDirty();
    }

    public boolean isHandDirty() {
        return playerZonesStatus.isHandDrity();
    }

    public boolean isDeckDirty() {
        return playerZonesStatus.isDeckDirty();
    }

    public boolean isDilemmaPileDirty() {
        return playerZonesStatus.isDilemmaPileDirty();
    }

    public boolean isDiscardPileDirty() {
        return playerZonesStatus.isDiscardPileDirty();
    }

    public void cleanup() {
        playerZonesStatus.cleanZones();
        for (MissionCards missionCard : missionCards) {
            missionCard.cleanup();
        }
    }

    public Entity removeCard(Entity card, CardZone oldZone) {
        if (oldZone == CardZone.Brig)
            return removeCardInBrig(card);
        if (oldZone == CardZone.Core)
            return removeCardInCore(card);
        if (oldZone == CardZone.Hand)
            return removeCardInHand(card);
        if (oldZone == CardZone.Mission) {
            for (MissionCards missionCard : missionCards) {
                Entity renderedCard = missionCard.removeCard(card);
                if (renderedCard != null)
                    return renderedCard;
            }
        }
        return null;
    }
}
