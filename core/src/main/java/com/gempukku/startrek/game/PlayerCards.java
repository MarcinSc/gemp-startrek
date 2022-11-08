package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerCards {
    private ObjectMap<Entity, Entity> cardToRenderedMap = new ObjectMap<>();
    private Array<Entity> cardsInHand = new Array<>();
    private Array<Entity> cardsInDeck = new Array<>();
    private Array<Entity> cardsInDilemmaPile = new Array<>();
    private Array<MissionCards> missionCards = new Array<>();

    public PlayerCards() {
        for (int i = 0; i < 5; i++) {
            missionCards.add(new MissionCards());
        }
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

    public Entity findRenderedCard(Entity cardEntity) {
        return cardToRenderedMap.get(cardEntity);
    }
}
