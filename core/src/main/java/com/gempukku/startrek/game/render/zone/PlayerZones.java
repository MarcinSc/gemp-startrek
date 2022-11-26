package com.gempukku.startrek.game.render.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.game.zone.CardZone;

public class PlayerZones {
    private final RenderedCardGroup cardsInHand;
    private final RenderedCardGroup cardsInCore;
    private final RenderedCardGroup cardsInBrig;

    private final RenderedCardGroup cardsInDeck;
    private final RenderedCardGroup cardsInDilemmaPile;

    private Entity topDiscardPileCard;
    private boolean discardPileDirty;

    private final Array<MissionCards> missionCards = new Array<>();
    private final ObjectMap<Entity, Entity> serverToRenderedMap;

    public PlayerZones(ObjectMap<Entity, Entity> serverToRenderedMap) {
        this.serverToRenderedMap = serverToRenderedMap;

        for (int i = 0; i < 5; i++) {
            missionCards.add(new MissionCards(serverToRenderedMap));
        }

        cardsInHand = new RenderedCardGroup(serverToRenderedMap);
        cardsInCore = new RenderedCardGroup(serverToRenderedMap);
        cardsInBrig = new RenderedCardGroup(serverToRenderedMap);
        cardsInDeck = new RenderedCardGroup(serverToRenderedMap);
        cardsInDilemmaPile = new RenderedCardGroup(serverToRenderedMap);
    }

    public Entity setTopDiscardPileCard(Entity card, Entity renderedCard) {
        Entity oldTopDiscardPileCard = topDiscardPileCard;
        serverToRenderedMap.put(card, renderedCard);
        topDiscardPileCard = renderedCard;
        discardPileDirty = true;
        return oldTopDiscardPileCard;
    }

    public Entity getTopDiscardPileCard() {
        return topDiscardPileCard;
    }

    public MissionCards getMissionCards(int missionIndex) {
        return missionCards.get(missionIndex);
    }

    public RenderedCardGroup getCardsInHand() {
        return cardsInHand;
    }

    public RenderedCardGroup getCardsInDeck() {
        return cardsInDeck;
    }

    public RenderedCardGroup getCardsInDilemmaPile() {
        return cardsInDilemmaPile;
    }

    public RenderedCardGroup getCardsInCore() {
        return cardsInCore;
    }

    public RenderedCardGroup getCardsInBrig() {
        return cardsInBrig;
    }

    public boolean isMissionDirty(int missionIndex) {
        return missionCards.get(missionIndex).isMissionDirty();
    }

    public boolean isBrigDirty() {
        return cardsInBrig.isDirty();
    }

    public boolean isCoreDirty() {
        return cardsInCore.isDirty();
    }

    public boolean isHandDirty() {
        return cardsInHand.isDirty();
    }

    public boolean isDeckDirty() {
        return cardsInDeck.isDirty();
    }

    public boolean isDilemmaPileDirty() {
        return cardsInDilemmaPile.isDirty();
    }

    public boolean isDiscardPileDirty() {
        return discardPileDirty;
    }

    public void cleanup() {
        for (MissionCards missionCard : missionCards) {
            missionCard.cleanup();
        }
        cardsInBrig.cleanup();
        cardsInCore.cleanup();
        cardsInHand.cleanup();
        cardsInDeck.cleanup();
        cardsInDilemmaPile.cleanup();
        discardPileDirty = false;
    }

    public Entity removeFaceUpCard(Entity card, CardZone oldZone) {
        if (oldZone == CardZone.Brig)
            return cardsInBrig.removeFaceUpCard(card);
        if (oldZone == CardZone.Core)
            return cardsInCore.removeFaceUpCard(card);
        if (oldZone == CardZone.Hand)
            return cardsInHand.removeFaceUpCard(card);
        if (oldZone == CardZone.Mission) {
            for (MissionCards missionCard : missionCards) {
                Entity renderedCard = missionCard.removeFaceUpCard(card);
                if (renderedCard != null)
                    return renderedCard;
            }
        }
        return null;
    }
}
