package com.gempukku.startrek.server.hall;

import com.artemis.BaseSystem;
import com.gempukku.startrek.hall.StarTrekDeck;

public class StarTrekServerDeckSystem extends BaseSystem {
    private StarTrekDeck temporaryDeck;

    public StarTrekServerDeckSystem() {
        temporaryDeck = new StarTrekDeck();
        temporaryDeck.setDeckId("deckId");
        temporaryDeck.setDeckName("Deep Space Nine Starter Deck");

        temporaryDeck.getDillemas().add("1_4");
        temporaryDeck.getDillemas().add("1_4");
        temporaryDeck.getDillemas().add("1_8");
        temporaryDeck.getDillemas().add("1_17");
        temporaryDeck.getDillemas().add("1_17");
        temporaryDeck.getDillemas().add("1_25");
        temporaryDeck.getDillemas().add("1_33");
        temporaryDeck.getDillemas().add("1_33");
        temporaryDeck.getDillemas().add("1_34");
        temporaryDeck.getDillemas().add("1_34");
        temporaryDeck.getDillemas().add("1_41");
        temporaryDeck.getDillemas().add("1_43");
        temporaryDeck.getDillemas().add("1_43");
        temporaryDeck.getDillemas().add("1_48");
        temporaryDeck.getDillemas().add("1_48");
        temporaryDeck.getDillemas().add("1_50");
        temporaryDeck.getDillemas().add("1_50");
        temporaryDeck.getDillemas().add("1_52");
        temporaryDeck.getDillemas().add("1_57");
        temporaryDeck.getDillemas().add("1_60");

        // Equipment
        temporaryDeck.getDrawDeck().add("1_68");

        // Event
        temporaryDeck.getDrawDeck().add("1_83");
        temporaryDeck.getDrawDeck().add("1_83");
        temporaryDeck.getDrawDeck().add("1_84");
        temporaryDeck.getDrawDeck().add("1_84");

        // Interrupts
        temporaryDeck.getDrawDeck().add("1_136");
        temporaryDeck.getDrawDeck().add("1_136");
        temporaryDeck.getDrawDeck().add("1_145");
        temporaryDeck.getDrawDeck().add("1_145");

        // Missions
        temporaryDeck.getMissions().add("1_170");
        temporaryDeck.getMissions().add("1_187");
        temporaryDeck.getMissions().add("1_188");
        temporaryDeck.getMissions().add("1_198");
        temporaryDeck.getMissions().add("1_199");

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
