package com.gempukku.startrek.server.hall;

import com.artemis.BaseSystem;
import com.gempukku.startrek.hall.StarTrekDeck;

public class StarTrekServerDeckSystem extends BaseSystem {
    private StarTrekDeck temporaryDeck;

    public StarTrekServerDeckSystem() {
        temporaryDeck = new StarTrekDeck();
        temporaryDeck.setDeckId("deckId");
        temporaryDeck.setDeckName("Deep Space Nine Starter Deck");

        temporaryDeck.getMissions().add("1_170");
        temporaryDeck.getMissions().add("1_187");
        temporaryDeck.getMissions().add("1_188");
        temporaryDeck.getMissions().add("1_198");
        temporaryDeck.getMissions().add("1_199");

        temporaryDeck.getDillemas().add("1_4");

        // Personnel - Bajoran
        temporaryDeck.getDrawDeck().add("1_207");
        temporaryDeck.getDrawDeck().add("1_207");
        temporaryDeck.getDrawDeck().add("1_210");
        temporaryDeck.getDrawDeck().add("1_210");
        temporaryDeck.getDrawDeck().add("1_214");
        temporaryDeck.getDrawDeck().add("1_214");
        temporaryDeck.getDrawDeck().add("1_217");
        temporaryDeck.getDrawDeck().add("1_225");
        temporaryDeck.getDrawDeck().add("1_225");
        // Personnel - Federation
        temporaryDeck.getDrawDeck().add("1_251");
        temporaryDeck.getDrawDeck().add("1_254");
        temporaryDeck.getDrawDeck().add("1_256");
        temporaryDeck.getDrawDeck().add("1_256");
        temporaryDeck.getDrawDeck().add("1_265");
        temporaryDeck.getDrawDeck().add("1_265");
        temporaryDeck.getDrawDeck().add("1_278");
        temporaryDeck.getDrawDeck().add("1_278");
        temporaryDeck.getDrawDeck().add("1_280");
        temporaryDeck.getDrawDeck().add("1_285");
        temporaryDeck.getDrawDeck().add("1_291");
        // Personnel - Non-Aligned
        temporaryDeck.getDrawDeck().add("1_320");
        temporaryDeck.getDrawDeck().add("1_334");
        temporaryDeck.getDrawDeck().add("1_351");
        // Ship - Federation
        temporaryDeck.getDrawDeck().add("1_390");
        temporaryDeck.getDrawDeck().add("1_390");
        temporaryDeck.getDrawDeck().add("1_390");
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
