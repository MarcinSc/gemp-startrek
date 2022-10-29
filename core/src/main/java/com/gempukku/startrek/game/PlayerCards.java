package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerCards {
    private ObjectMap<Entity, Entity> cardToRenderedMap = new ObjectMap<>();
    private Array<Entity> renderedInHand = new Array<>();
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

    public MissionCards getMissionCards(int missionIndex) {
        return missionCards.get(missionIndex);
    }

    public Entity removeOneCardInHand() {
        return renderedInHand.removeIndex(renderedInHand.size - 1);
    }

    public Entity removeCardInHand(Entity card) {
        Entity renderedCard = cardToRenderedMap.remove(card);
        renderedInHand.removeValue(renderedCard, true);
        return renderedCard;
    }

    public int getCardInHandCount() {
        return renderedInHand.size;
    }

    public Array<Entity> getCardsInHand() {
        return renderedInHand;
    }
}
