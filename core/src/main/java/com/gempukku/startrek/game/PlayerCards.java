package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerCards {
    private ObjectMap<Entity, Entity> cardToRenderedMap = new ObjectMap<>();
    private Array<Entity> renderedInHand = new Array<>();
    private Array<Entity> renderedInDeck = new Array<>();
    private Array<Entity> renderedInDilemmaPile = new Array<>();
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
        renderedInHand.add(renderedCard);
    }

    public void addCardInDeck(Entity card, Entity renderedCard) {
        if (card != null) {
            cardToRenderedMap.put(card, renderedCard);
        }
        renderedInDeck.add(renderedCard);
    }

    public void addCardInDilemmaPile(Entity card, Entity renderedCard) {
        if (card != null) {
            cardToRenderedMap.put(card, renderedCard);
        }
        renderedInDilemmaPile.add(renderedCard);
    }

    public MissionCards getMissionCards(int missionIndex) {
        return missionCards.get(missionIndex);
    }

    public Entity removeOneCardInHand() {
        return removeLast(renderedInHand);
    }

    public Entity removeCardInHand(Entity card) {
        Entity renderedCard = cardToRenderedMap.remove(card);
        renderedInHand.removeValue(renderedCard, true);
        return renderedCard;
    }

    public Entity removeOneCardInDeck() {
        return removeLast(renderedInDeck);
    }

    public Entity removeOneCardInDilemmaPile() {
        return removeLast(renderedInDilemmaPile);
    }

    private Entity removeLast(Array<Entity> deck) {
        return deck.removeIndex(deck.size - 1);
    }

    public int getCardInHandCount() {
        return renderedInHand.size;
    }

    public int getCardInDeckCount() {
        return renderedInDeck.size;
    }

    public int getCardInDilemmaCount() {
        return renderedInDilemmaPile.size;
    }

    public Array<Entity> getCardsInHand() {
        return renderedInHand;
    }

    public Array<Entity> getCardsInDeck() {
        return renderedInDeck;
    }

    public Array<Entity> getCardsInDilemmaPile() {
        return renderedInDilemmaPile;
    }
}
