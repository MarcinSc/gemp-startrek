package com.gempukku.startrek.game.decision.ui;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;

public class CardContainer {
    private Array<Entity> cards = new Array<>();

    public void addCard(Entity renderedCard) {
        cards.add(renderedCard);
    }

    public void removeCard(Entity renderedCard) {
        cards.removeValue(renderedCard, true);
    }
}
