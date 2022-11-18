package com.gempukku.startrek.game.render.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MissionCards {
    private boolean missionDirty = false;

    private final ObjectMap<Entity, Entity> renderedCards = new ObjectMap<>();
    private final Array<Entity> playerTopLevelCardsInMission = new Array<>();
    private final Array<Entity> opponentTopLevelCardsInMission = new Array<>();
    private final ObjectMap<Entity, Array<Entity>> attachedCardsInMission = new ObjectMap<>();
    private final Array<Entity> missionCards = new Array<>();
    private final Array<Entity> faceDownPlayerCards = new Array<>();
    private final Array<Entity> faceDownOpponentCards = new Array<>();

    public int getFaceDownPlayerCardCount() {
        return faceDownPlayerCards.size;
    }

    public int getFaceDownOpponentCardCount() {
        return faceDownOpponentCards.size;
    }

    public void addMissionCard(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        attachedCardsInMission.put(renderedCard, new Array<>());
        missionCards.add(renderedCard);

        missionDirty = true;
    }

    public Array<Entity> getMissionCards() {
        return missionCards;
    }

    public Entity findRenderedCard(Entity card) {
        return renderedCards.get(card);
    }

    public Array<Entity> getAttachedCards(Entity renderedCard) {
        return attachedCardsInMission.get(renderedCard);
    }

    public void addPlayerTopLevelCardInMission(Entity card, Entity renderedCard) {
        if (card != null) {
            renderedCards.put(card, renderedCard);
        } else {
            faceDownPlayerCards.add(renderedCard);
        }
        attachedCardsInMission.put(renderedCard, new Array<>());
        playerTopLevelCardsInMission.add(renderedCard);

        missionDirty = true;
    }

    public Array<Entity> getPlayerTopLevelCardsInMission() {
        return playerTopLevelCardsInMission;
    }

    public Entity removeFaceDownPlayerCard() {
        return faceDownPlayerCards.removeIndex(faceDownPlayerCards.size - 1);
    }

    public Entity removePlayerTopLevelCardInMission(Entity card) {
        Entity renderedCard = renderedCards.remove(card);
        playerTopLevelCardsInMission.removeValue(renderedCard, true);
        attachedCardsInMission.remove(renderedCard);
        missionDirty = true;
        return renderedCard;
    }

    public void addOpponentTopLevelCardInMission(Entity card, Entity renderedCard) {
        if (card != null) {
            renderedCards.put(card, renderedCard);
        } else {
            faceDownOpponentCards.add(renderedCard);
        }
        attachedCardsInMission.put(renderedCard, new Array<>());
        opponentTopLevelCardsInMission.add(renderedCard);

        missionDirty = true;
    }

    public Array<Entity> getOpponentTopLevelCardsInMission() {
        return opponentTopLevelCardsInMission;
    }

    public Entity removeFaceDownOpponentCard() {
        return faceDownOpponentCards.removeIndex(faceDownOpponentCards.size - 1);
    }

    public Entity removeOpponentTopLevelCardInMission(Entity card) {
        Entity renderedCard = renderedCards.remove(card);
        opponentTopLevelCardsInMission.removeValue(renderedCard, true);
        attachedCardsInMission.remove(renderedCard);
        missionDirty = true;
        return renderedCard;
    }

    public void cleanup() {
        missionDirty = false;
    }

    public boolean isMissionDirty() {
        return missionDirty;
    }

    public Entity removeCard(Entity card) {
        Entity renderedCard = renderedCards.remove(card);
        if (renderedCard != null) {
            attachedCardsInMission.remove(renderedCard);
            playerTopLevelCardsInMission.removeValue(renderedCard, true);
            opponentTopLevelCardsInMission.removeValue(renderedCard, true);
            missionDirty = true;
            return renderedCard;
        }
        return null;
    }

    //
//    public void addAttachedCardInMission(Entity card, Entity attachedTo, Entity renderedCard) {
//        renderedCards.put(card, renderedCard);
//        Array<Entity> attachedCards = attachedCardsInMission.get(attachedTo);
//        attachedCards.add(renderedCard);
//    }
//
//    public Entity removeAttachedCardInMission(Entity card, Entity attachedTo) {
//        Entity rendered = renderedCards.remove(card);
//        Array<Entity> attachedCards = attachedCardsInMission.get(attachedTo);
//        attachedCards.removeValue(card, true);
//        return rendered;
//    }
}
