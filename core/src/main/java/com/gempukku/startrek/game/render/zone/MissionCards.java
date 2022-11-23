package com.gempukku.startrek.game.render.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public class MissionCards {
    private final RenderedCardGroup missionCards;
    private final RenderedCardGroup missionOwnerCards;
    private final RenderedCardGroup opposingCards;

    public MissionCards(ObjectMap<Entity, Entity> serverToRenderedCards,
                        ObjectMap<Entity, RenderedCardGroup> attachedCards) {
        missionCards = new RenderedCardGroup(serverToRenderedCards, attachedCards);
        missionOwnerCards = new RenderedCardGroup(serverToRenderedCards, attachedCards);
        opposingCards = new RenderedCardGroup(serverToRenderedCards, attachedCards);
    }

    public RenderedCardGroup getMissionCards() {
        return missionCards;
    }

    public RenderedCardGroup getMissionOwnerCards() {
        return missionOwnerCards;
    }

    public RenderedCardGroup getOpposingCards() {
        return opposingCards;
    }

    public RenderedCardGroup getAttachedCards(Entity renderedCard) {
        RenderedCardGroup result = missionCards.getAttachedCards(renderedCard);
        if (result == null)
            result = missionOwnerCards.getAttachedCards(renderedCard);
        if (result == null)
            result = opposingCards.getAttachedCards(renderedCard);
        return result;
    }

    public void cleanup() {
        missionCards.cleanup();
        missionOwnerCards.cleanup();
        opposingCards.cleanup();
    }

    public boolean isMissionDirty() {
        return missionCards.isDirty() || missionOwnerCards.isDirty() || opposingCards.isDirty();
    }

    public Entity removeFaceUpCard(Entity card) {
        Entity result = missionCards.removeFaceUpCard(card);
        if (result == null)
            result = missionOwnerCards.removeFaceUpCard(card);
        if (result == null)
            result = opposingCards.removeFaceUpCard(card);
        return result;
    }
}
