package com.gempukku.startrek.game.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MissionCards {
    private final ObjectMap<Entity, Entity> renderedCards = new ObjectMap<>();
    private final Array<Entity> playerTopLevelCardsInMission = new Array<>();
    private final Array<Entity> opponentTopLevelCardsInMission = new Array<>();
    private final ObjectMap<Entity, Array<Entity>> attachedCardsInMission = new ObjectMap<>();
    private final Array<Entity> missionCards = new Array<>();

    public void addMissionCard(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        attachedCardsInMission.put(renderedCard, new Array<>());
        missionCards.add(renderedCard);
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
        renderedCards.put(card, renderedCard);
        attachedCardsInMission.put(renderedCard, new Array<>());
        playerTopLevelCardsInMission.add(renderedCard);
    }

    public Array<Entity> getPlayerTopLevelCardsInMission() {
        return playerTopLevelCardsInMission;
    }

    public Entity removePlayerTopLevelCardInMission(Entity card) {
        Entity renderedCard = renderedCards.remove(card);
        playerTopLevelCardsInMission.removeValue(renderedCard, true);
        return renderedCard;
    }

    public void addOpponentTopLevelCardInMission(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        attachedCardsInMission.put(renderedCard, new Array<>());
        opponentTopLevelCardsInMission.add(renderedCard);
    }

    public Array<Entity> getOpponentTopLevelCardsInMission() {
        return opponentTopLevelCardsInMission;
    }

    public Entity removeOpponentTopLevelCardInMission(Entity card) {
        Entity renderedCard = renderedCards.remove(card);
        opponentTopLevelCardsInMission.removeValue(renderedCard, true);
        return renderedCard;
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
