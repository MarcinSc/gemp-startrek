package com.gempukku.startrek.game.decision.ui;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;

public class CardContainer {
    private World world;
    private SpawnSystem spawnSystem;

    private CardContainerSettings cardContainerSettings;

    private Array<Entity> cards = new Array<>();

    public CardContainer(World world, SpawnSystem spawnSystem, CardContainerSettings cardContainerSettings) {
        this.world = world;
        this.spawnSystem = spawnSystem;
        this.cardContainerSettings = cardContainerSettings;

        cards.add(createShadowPickable());
    }

    public CardContainerSettings getCardContainerSettings() {
        return cardContainerSettings;
    }

    public void addCard(Entity renderedCard) {
        cards.add(renderedCard);
        cards.add(createShadowPickable());
    }

    public void insertCardAfter(Entity renderedCard, Entity card) {
        int cardIndex = cards.indexOf(card, true);
        if (cardIndex >= 0) {
            cards.insert(cardIndex + 1, renderedCard);
            cards.insert(cardIndex + 2, createShadowPickable());
        }
    }

    public boolean removeCard(Entity renderedCard) {
        int cardIndex = cards.indexOf(renderedCard, true);
        if (cardIndex < 0)
            return false;
        cards.removeIndex(cardIndex);
        world.deleteEntity(cards.removeIndex(cardIndex));
        return true;
    }

    public Array<Entity> getCards() {
        return cards;
    }

    private Entity createShadowPickable() {
        return spawnSystem.spawnEntity("game/ui/card-shadow-pickable.template");
    }
}
