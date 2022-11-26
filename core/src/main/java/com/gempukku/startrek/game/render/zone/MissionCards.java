package com.gempukku.startrek.game.render.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public class MissionCards {
    private final RenderedCardGroup missionCards;
    private final RenderedCardGroup missionOwnerCards;
    private final RenderedCardGroup opposingCards;

    public MissionCards(ObjectMap<Entity, Entity> serverToRenderedCards) {
        missionCards = new RenderedCardGroup(serverToRenderedCards);
        missionOwnerCards = new RenderedCardGroup(serverToRenderedCards);
        opposingCards = new RenderedCardGroup(serverToRenderedCards);
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
