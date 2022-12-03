package com.gempukku.startrek.game.render.zone;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class RenderedCardGroup {
    private final ObjectMap<Entity, Entity> serverToRenderedMap;
    private final Array<Entity> renderedCards = new Array<>();
    private final Array<Entity> faceDownCards = new Array<>();

    private boolean dirty;

    public RenderedCardGroup(ObjectMap<Entity, Entity> serverToRenderedMap) {
        this.serverToRenderedMap = serverToRenderedMap;
    }

    public void addFaceDownCard(Entity renderedEntity) {
        renderedCards.add(renderedEntity);
        faceDownCards.add(renderedEntity);
        dirty = true;
    }

    public Entity removeFaceDownCard() {
        if (faceDownCards.size == 0)
            return null;
        Entity renderedEntity = faceDownCards.removeIndex(faceDownCards.size - 1);
        renderedCards.removeValue(renderedEntity, true);
        dirty = true;
        return renderedEntity;
    }

    public void addFaceUpCard(Entity cardEntity, Entity renderedEntity) {
        serverToRenderedMap.put(cardEntity, renderedEntity);
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

    public void setDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void cleanup() {
        dirty = false;
    }

    public boolean isEmpty() {
        return renderedCards.isEmpty();
    }
}
