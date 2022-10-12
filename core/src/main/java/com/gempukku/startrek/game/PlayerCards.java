package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerCards {
    private Array<Entity> renderedCardsInHand = new Array<>();
    private ObjectMap<Entity, Entity> renderedCards = new ObjectMap<>();

    public void addCardInHand(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        renderedCardsInHand.add(renderedCard);
    }

    public void removeCardInHand(Entity card) {
        Entity renderedCard = renderedCards.remove(card);
        renderedCardsInHand.removeValue(renderedCard, true);
    }

    public Array<Entity> getCardsInHand() {
        return renderedCardsInHand;
    }
}
