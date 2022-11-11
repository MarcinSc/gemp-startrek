package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerZones {
    private final ObjectMap<Entity, Entity> cardToRenderedMap = new ObjectMap<>();
    private final Array<Entity> cardsInHand = new Array<>();
    private final Array<Entity> cardsInDeck = new Array<>();
    private final Array<Entity> cardsInDilemmaPile = new Array<>();
    private final Array<Entity> cardsInCore = new Array<>();

    private final Array<MissionCards> missionCards = new Array<>();

    public PlayerZones() {
        for (int i = 0; i < 5; i++) {
            missionCards.add(new MissionCards());
        }
    }

    public void addCardInCore(Entity card, Entity renderedCard) {
        if (card != null) {
            cardToRenderedMap.put(card, renderedCard);
        }
        cardsInCore.add(renderedCard);
    }

    public void addCardInHand(Entity card, Entity renderedCard) {
        if (card != null) {
            cardToRenderedMap.put(card, renderedCard);
        }
        cardsInHand.add(renderedCard);
    }

    public void addCardInDeck(Entity card, Entity renderedCard) {
        if (card != null) {
            cardToRenderedMap.put(card, renderedCard);
        }
        cardsInDeck.add(renderedCard);
    }

    public void addCardInDilemmaPile(Entity card, Entity renderedCard) {
        if (card != null) {
            cardToRenderedMap.put(card, renderedCard);
        }
        cardsInDilemmaPile.add(renderedCard);
    }

    public MissionCards getMissionCards(int missionIndex) {
        return missionCards.get(missionIndex);
    }

    public Entity removeOneCardInHand() {
        return removeLast(cardsInHand);
    }

    public Entity removeCardInHand(Entity card) {
        Entity renderedCard = cardToRenderedMap.remove(card);
        cardsInHand.removeValue(renderedCard, true);
        return renderedCard;
    }

    public Entity removeOneCardInDeck() {
        return removeLast(cardsInDeck);
    }

    public Entity removeOneCardInDilemmaPile() {
        return removeLast(cardsInDilemmaPile);
    }

    public Entity removeCardInCore(Entity card) {
        Entity renderedCard = cardToRenderedMap.remove(card);
        cardsInCore.removeValue(renderedCard, true);
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
}
