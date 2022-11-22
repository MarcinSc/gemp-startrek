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
        Entity renderedCard = faceDownPlayerCards.removeIndex(faceDownPlayerCards.size - 1);
        playerTopLevelCardsInMission.removeValue(renderedCard, true);
        return renderedCard;
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
        Entity renderedCard = faceDownOpponentCards.removeIndex(faceDownPlayerCards.size - 1);
        opponentTopLevelCardsInMission.removeValue(renderedCard, true);
        return renderedCard;
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

    public void addAttachedCard(Entity attachedToCardEntity, Entity cardEntity, Entity cardRepresentation) {
        if (cardEntity != null) {
            renderedCards.put(cardEntity, cardRepresentation);
        }
        Entity renderedAttachedTo = renderedCards.get(attachedToCardEntity);
        attachedCardsInMission.get(renderedAttachedTo).add(cardRepresentation);
        missionDirty = true;
    }

    public Entity removeAttachedCard(Entity attachedToCardEntity, Entity cardEntity) {
        Entity renderedEntity = renderedCards.remove(cardEntity);
        Entity renderedAttachedTo = renderedCards.get(attachedToCardEntity);
        attachedCardsInMission.get(renderedAttachedTo).removeValue(renderedEntity, true);
        missionDirty = true;
        return renderedEntity;
    }

    public Entity removeFaceDownAttachedCard(Entity attachedToCardEntity) {
        Entity renderedAttachedTo = renderedCards.get(attachedToCardEntity);
        Array<Entity> entities = attachedCardsInMission.get(renderedAttachedTo);
        missionDirty = true;
        return entities.removeIndex(entities.size - 1);
    }
}
