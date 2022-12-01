package com.gempukku.startrek.game.decision.ui;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;

public class CardContainer {
    private SpawnSystem spawnSystem;

    private Array<Entity> cards = new Array<>();

    public CardContainer(SpawnSystem spawnSystem) {
        this.spawnSystem = spawnSystem;
        cards.add(createShadowPickable());
    }

    public void addCard(Entity renderedCard) {
        cards.add(renderedCard);
        cards.add(createShadowPickable());
    }

    public boolean removeCard(Entity renderedCard) {
        int cardIndex = cards.indexOf(renderedCard, true);
        if (cardIndex < 0)
            return false;
        cards.removeIndex(cardIndex);
        cards.removeIndex(cardIndex);
        return true;
    }

    private Entity createShadowPickable() {
        return spawnSystem.spawnEntity("game/card/ui/card-shadow-pickable.template");
    }
}
