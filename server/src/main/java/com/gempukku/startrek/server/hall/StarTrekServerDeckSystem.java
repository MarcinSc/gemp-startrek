package com.gempukku.startrek.server.hall;

import com.artemis.BaseSystem;
import com.gempukku.startrek.hall.StarTrekDeck;

public class StarTrekServerDeckSystem extends BaseSystem {
    private StarTrekDeck temporaryDeck;

    public StarTrekServerDeckSystem() {
        //this.temporaryDeck = createTestDeck();
        this.temporaryDeck = createDeepSpaceNineStarterDeck();
    }

    private StarTrekDeck createTestDeck() {
        StarTrekDeck deck = new StarTrekDeck();
        deck.setDeckId("deckId");
        deck.setDeckName("Deep Space Nine Starter Deck");

        addBasicDillemas(deck);
        addBasicMissions(deck);

        deck.getDrawDeck().add("1_83");

        return deck;
    }

    private void addBasicMissions(StarTrekDeck deck) {
        // Missions
        deck.getMissions().add("1_170");
        deck.getMissions().add("1_187");
        deck.getMissions().add("1_188");
        deck.getMissions().add("1_198");
        deck.getMissions().add("1_199");
    }

    private void addBasicDillemas(StarTrekDeck deck) {
        deck.getDillemas().add("1_4");
        deck.getDillemas().add("1_4");
        deck.getDillemas().add("1_8");
        deck.getDillemas().add("1_17");
        deck.getDillemas().add("1_17");
        deck.getDillemas().add("1_25");
        deck.getDillemas().add("1_33");
        deck.getDillemas().add("1_33");
        deck.getDillemas().add("1_34");
        deck.getDillemas().add("1_34");
        deck.getDillemas().add("1_41");
        deck.getDillemas().add("1_43");
        deck.getDillemas().add("1_43");
        deck.getDillemas().add("1_48");
        deck.getDillemas().add("1_48");
        deck.getDillemas().add("1_50");
        deck.getDillemas().add("1_50");
        deck.getDillemas().add("1_52");
        deck.getDillemas().add("1_57");
        deck.getDillemas().add("1_60");
    }

    private StarTrekDeck createDeepSpaceNineStarterDeck() {
        StarTrekDeck deck = new StarTrekDeck();
        deck.setDeckId("deckId");
        deck.setDeckName("Deep Space Nine Starter Deck");

        deck.getDillemas().add("1_4");
        deck.getDillemas().add("1_4");
        deck.getDillemas().add("1_8");
        deck.getDillemas().add("1_17");
        deck.getDillemas().add("1_17");
        deck.getDillemas().add("1_25");
        deck.getDillemas().add("1_33");
        deck.getDillemas().add("1_33");
        deck.getDillemas().add("1_34");
        deck.getDillemas().add("1_34");
        deck.getDillemas().add("1_41");
        deck.getDillemas().add("1_43");
        deck.getDillemas().add("1_43");
        deck.getDillemas().add("1_48");
        deck.getDillemas().add("1_48");
        deck.getDillemas().add("1_50");
        deck.getDillemas().add("1_50");
        deck.getDillemas().add("1_52");
        deck.getDillemas().add("1_57");
        deck.getDillemas().add("1_60");

        // Equipment
        deck.getDrawDeck().add("1_68");

        // Event
        deck.getDrawDeck().add("1_83");
        deck.getDrawDeck().add("1_83");
        deck.getDrawDeck().add("1_84");
        deck.getDrawDeck().add("1_84");

        // Interrupts
        deck.getDrawDeck().add("1_136");
        deck.getDrawDeck().add("1_136");
        deck.getDrawDeck().add("1_145");
        deck.getDrawDeck().add("1_145");

        // Missions
        deck.getMissions().add("1_170");
        deck.getMissions().add("1_187");
        deck.getMissions().add("1_188");
        deck.getMissions().add("1_198");
        deck.getMissions().add("1_199");

        // Personnel - Bajoran
        deck.getDrawDeck().add("1_207");
        deck.getDrawDeck().add("1_207");
        deck.getDrawDeck().add("1_210");
        deck.getDrawDeck().add("1_210");
        deck.getDrawDeck().add("1_214");
        deck.getDrawDeck().add("1_214");
        deck.getDrawDeck().add("1_217");
        deck.getDrawDeck().add("1_225");
        deck.getDrawDeck().add("1_225");
        // Personnel - Federation
        deck.getDrawDeck().add("1_251");
        deck.getDrawDeck().add("1_254");
        deck.getDrawDeck().add("1_256");
        deck.getDrawDeck().add("1_256");
        deck.getDrawDeck().add("1_265");
        deck.getDrawDeck().add("1_265");
        deck.getDrawDeck().add("1_278");
        deck.getDrawDeck().add("1_278");
        deck.getDrawDeck().add("1_280");
        deck.getDrawDeck().add("1_285");
        deck.getDrawDeck().add("1_291");
        // Personnel - Non-Aligned
        deck.getDrawDeck().add("1_320");
        deck.getDrawDeck().add("1_334");
        deck.getDrawDeck().add("1_351");
        // Ship - Federation
        deck.getDrawDeck().add("1_390");
        deck.getDrawDeck().add("1_390");
        deck.getDrawDeck().add("1_390");
        return deck;
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
