package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MissionCards {
    private ObjectMap<Entity, Entity> renderedCards = new ObjectMap<>();
    private Array<Entity> cardInMission = new Array<>();
    private Entity renderedMission;

    public void setMissionCard(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        this.renderedMission = renderedCard;
    }

    public Entity getMissionCard() {
        return renderedMission;
    }

    public void addCardInMission(Entity card, Entity renderedCard) {
        renderedCards.put(card, renderedCard);
        cardInMission.add(renderedCard);
    }

    public void removeCardInMission(Entity card) {
        Entity rendered = renderedCards.remove(card);
        cardInMission.removeValue(rendered, true);
    }
}
