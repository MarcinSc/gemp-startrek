package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MissionCards {
    private ObjectMap<Entity, Entity> renderedCards = new ObjectMap<>();
    private Array<Entity> playerTopLevelCardsInMission = new Array<>();
    private Array<Entity> opponentTopLevelCardsInMission = new Array<>();
    private ObjectMap<Entity, Array<Entity>> attachedCardsInMission = new ObjectMap<>();
    private Entity missionCard;

    public void setMissionCard(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        attachedCardsInMission.put(card, new Array<>());
        this.missionCard = card;
    }

    public Entity getMissionCard() {
        return missionCard;
    }

    public Entity getRenderedCard(Entity card) {
        return renderedCards.get(card);
    }

    public Array<Entity> getAttachedCards(Entity card) {
        return attachedCardsInMission.get(card);
    }

    public void addPlayerTopLevelCardInMission(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        attachedCardsInMission.put(card, new Array<>());
        playerTopLevelCardsInMission.add(card);
    }

    public Array<Entity> getPlayerTopLevelCardsInMission() {
        return playerTopLevelCardsInMission;
    }

    public Entity removePlayerTopLevelCardInMission(Entity card) {
        Entity rendered = renderedCards.remove(card);
        playerTopLevelCardsInMission.removeValue(card, true);
        return rendered;
    }

    public void addOpponentTopLevelCardInMission(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        attachedCardsInMission.put(card, new Array<>());
        opponentTopLevelCardsInMission.add(card);
    }

    public Array<Entity> getOpponentTopLevelCardsInMission() {
        return opponentTopLevelCardsInMission;
    }

    public Entity removeOpponentTopLevelCardInMission(Entity card) {
        Entity rendered = renderedCards.remove(card);
        opponentTopLevelCardsInMission.removeValue(card, true);
        return rendered;
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
