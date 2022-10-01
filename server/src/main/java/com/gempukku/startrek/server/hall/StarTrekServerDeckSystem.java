package com.gempukku.startrek.server.hall;

import com.artemis.BaseSystem;
import com.gempukku.startrek.hall.StarTrekDeck;

public class StarTrekServerDeckSystem extends BaseSystem {
    private StarTrekDeck temporaryDeck;

    public StarTrekServerDeckSystem() {
        temporaryDeck = new StarTrekDeck();
        temporaryDeck.setDeckId("deckId");
        temporaryDeck.setDeckName("Example deck");

        temporaryDeck.getMissions().add("12_40");
        temporaryDeck.getMissions().add("12_42");
        temporaryDeck.getMissions().add("29_36");
        temporaryDeck.getMissions().add("7_45");
        temporaryDeck.getMissions().add("30_32");

        temporaryDeck.getDillemas().add("37_2");

        temporaryDeck.getDrawDeck().add("12_58");
    }

    public StarTrekDeck getStarterDeck(String starterDeckId) {
        return temporaryDeck;
    }

    public StarTrekDeck getPlayerDeck(String username, String playerDeckId) {
        return temporaryDeck;
    }

    @Override
    protected void processSystem() {

    }
}
