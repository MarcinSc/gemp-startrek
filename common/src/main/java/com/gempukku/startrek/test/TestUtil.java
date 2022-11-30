package com.gempukku.startrek.test;

import com.artemis.Entity;
import com.artemis.World;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.hall.StarTrekDeck;

public class TestUtil {
    public static StarTrekDeck createDeckWithMissions(CardData cardData, String... cardIds) {
        StarTrekDeck deck = new StarTrekDeck();
        for (String cardId : cardIds) {
            CardDefinition cardDefinition = cardData.getCardDefinition(cardId);
            CardType type = cardDefinition.getType();
            if (type == CardType.Dilemma)
                deck.getDillemas().add(cardId);
            else if (type == CardType.Mission)
                deck.getMissions().add(cardId);
            else
                deck.getDrawDeck().add(cardId);
        }

        // Missions
        deck.getMissions().add("1_170");
        deck.getMissions().add("1_187");
        deck.getMissions().add("1_188");
        deck.getMissions().add("1_198");
        deck.getMissions().add("1_199");

        return deck;
    }

    public static Entity createCard(World world, String owner, String cardId) {
        CardLookupSystem cardLookupSystem = world.getSystem(CardLookupSystem.class);
        SpawnSystem spawnSystem = world.getSystem(SpawnSystem.class);

        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
        if (cardDefinition == null)
            throw new RuntimeException("Unable to locate card with id: " + cardId);

        Entity cardEntity = spawnSystem.spawnEntity("game/card.template");
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        card.setOwner(owner);
        card.setCardId(cardId);
        return cardEntity;
    }
}
