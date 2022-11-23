package com.gempukku.startrek.game.render.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class RenderedCardGroup {
    private final ObjectMap<Entity, Entity> serverToRenderedMap;
    private final ObjectMap<Entity, RenderedCardGroup> attachedCards;
    private final Array<Entity> renderedCards = new Array<>();
    private final Array<Entity> faceDownCards = new Array<>();

    private boolean dirty;

    public RenderedCardGroup(ObjectMap<Entity, Entity> serverToRenderedMap,
                             ObjectMap<Entity, RenderedCardGroup> attachedCards) {
        this.serverToRenderedMap = serverToRenderedMap;
        this.attachedCards = attachedCards;
    }

    public void addFaceDownCard(Entity renderedEntity) {
        renderedCards.add(renderedEntity);
        attachedCards.put(renderedEntity, new RenderedCardGroup(serverToRenderedMap, attachedCards));
        faceDownCards.add(renderedEntity);
        dirty = true;
    }

    public Entity removeFaceDownCard() {
        if (faceDownCards.size == 0)
            return null;
        Entity removedCard = faceDownCards.removeIndex(faceDownCards.size - 1);
        attachedCards.remove(removedCard);
        renderedCards.removeValue(removedCard, true);
        dirty = true;
        return removedCard;
    }

    public void addFaceUpCard(Entity cardEntity, Entity renderedEntity) {
        serverToRenderedMap.put(cardEntity, renderedEntity);
        attachedCards.put(renderedEntity, new RenderedCardGroup(serverToRenderedMap, attachedCards));
        renderedCards.add(renderedEntity);
        dirty = true;
    }

    public Entity removeFaceUpCard(Entity cardEntity) {
        if (hasServerCard(cardEntity)) {
            Entity renderedEntity = serverToRenderedMap.remove(cardEntity);
            renderedCards.removeValue(renderedEntity, true);
            dirty = true;
            return renderedEntity;
        }
        return null;
    }

    public void addAttachedFaceUpCard(Entity attachedToCardEntity, Entity attachedCardEntity, Entity renderedCard) {
        if (hasServerCard(attachedToCardEntity)) {
            Entity attachedTo = serverToRenderedMap.get(attachedToCardEntity);
            attachedCards.get(attachedTo).addFaceUpCard(attachedCardEntity, renderedCard);
            dirty = true;
        }
    }

    public Entity removeAttachedFaceUpCard(Entity attachedToCardEntity, Entity attachedCardEntity) {
        if (hasServerCard(attachedToCardEntity)) {
            Entity attachedTo = serverToRenderedMap.get(attachedToCardEntity);
            dirty = true;
            return attachedCards.get(attachedTo).removeFaceUpCard(attachedCardEntity);
        }
        return null;
    }

    public void addAttachedFaceDownCard(Entity attachedToCardEntity, Entity renderedCard) {
        if (hasServerCard(attachedToCardEntity)) {
            Entity attachedTo = serverToRenderedMap.get(attachedToCardEntity);
            attachedCards.get(attachedTo).addFaceDownCard(renderedCard);
            dirty = true;
        }
    }

    public Entity removeAttachedFaceDownCard(Entity attachedToCardEntity) {
        if (hasServerCard(attachedToCardEntity)) {
            Entity attachedTo = serverToRenderedMap.get(attachedToCardEntity);
            dirty = true;
            return attachedCards.get(attachedTo).removeFaceDownCard();
        }
        return null;
    }

    public RenderedCardGroup getAttachedCards(Entity renderedEntity) {
        if (hasRenderedCard(renderedEntity))
            return attachedCards.get(renderedEntity);
        return null;
    }

    public boolean hasServerCard(Entity cardEntity) {
        Entity renderedEntity = serverToRenderedMap.get(cardEntity);
        return renderedCards.contains(renderedEntity, true);
    }

    public boolean hasRenderedCard(Entity renderedEntity) {
        return renderedCards.contains(renderedEntity, true);
    }

    public Array<Entity> getRenderedCards() {
        return renderedCards;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void cleanup() {
        dirty = false;
    }
}
