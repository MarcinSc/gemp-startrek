package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public class MissionCards {
    private ObjectMap<Entity, Entity> renderedCards = new ObjectMap<>();
    private Entity renderedMission;

    public void setMissionCard(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        this.renderedMission = renderedCard;
    }
}
