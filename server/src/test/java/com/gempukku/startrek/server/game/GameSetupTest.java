package com.gempukku.startrek.server.game;

import com.gempukku.startrek.hall.StarTrekDeck;
import org.junit.Test;

public class GameSetupTest extends AbstractGameTest {
    @Test
    public void gameSetupTest() {
        StarTrekDeck testDeck = createTestDeck();
        setupGame(testDeck, testDeck);

        sendDecision("test1");
    }

    private StarTrekDeck createTestDeck() {
        StarTrekDeck testDeck = new StarTrekDeck();
        testDeck.getDillemas().add("1_4");
        testDeck.getDillemas().add("1_4");
        testDeck.getDillemas().add("1_8");
        testDeck.getDillemas().add("1_17");
        testDeck.getDillemas().add("1_17");
        testDeck.getDillemas().add("1_25");
        testDeck.getDillemas().add("1_33");
        testDeck.getDillemas().add("1_33");
        testDeck.getDillemas().add("1_34");
        testDeck.getDillemas().add("1_34");
        testDeck.getDillemas().add("1_41");
        testDeck.getDillemas().add("1_43");
        testDeck.getDillemas().add("1_43");
        testDeck.getDillemas().add("1_48");
        testDeck.getDillemas().add("1_48");
        testDeck.getDillemas().add("1_50");
        testDeck.getDillemas().add("1_50");
        testDeck.getDillemas().add("1_52");
        testDeck.getDillemas().add("1_57");
        testDeck.getDillemas().add("1_60");

        // Equipment
        testDeck.getDrawDeck().add("1_68");

        // Event
        testDeck.getDrawDeck().add("1_83");
        testDeck.getDrawDeck().add("1_83");
        testDeck.getDrawDeck().add("1_84");
        testDeck.getDrawDeck().add("1_84");

        // Interrupts
        testDeck.getDrawDeck().add("1_136");
        testDeck.getDrawDeck().add("1_136");
        testDeck.getDrawDeck().add("1_145");
        testDeck.getDrawDeck().add("1_145");

        // Missions
        testDeck.getMissions().add("1_170");
        testDeck.getMissions().add("1_187");
        testDeck.getMissions().add("1_188");
        testDeck.getMissions().add("1_198");
        testDeck.getMissions().add("1_199");

        // Personnel - Bajoran
        testDeck.getDrawDeck().add("1_207");
        testDeck.getDrawDeck().add("1_207");
        testDeck.getDrawDeck().add("1_210");
        testDeck.getDrawDeck().add("1_210");
        testDeck.getDrawDeck().add("1_214");
        testDeck.getDrawDeck().add("1_214");
        testDeck.getDrawDeck().add("1_217");
        testDeck.getDrawDeck().add("1_225");
        testDeck.getDrawDeck().add("1_225");
        // Personnel - Federation
        testDeck.getDrawDeck().add("1_251");
        testDeck.getDrawDeck().add("1_254");
        testDeck.getDrawDeck().add("1_256");
        testDeck.getDrawDeck().add("1_256");
        testDeck.getDrawDeck().add("1_265");
        testDeck.getDrawDeck().add("1_265");
        testDeck.getDrawDeck().add("1_278");
        testDeck.getDrawDeck().add("1_278");
        testDeck.getDrawDeck().add("1_280");
        testDeck.getDrawDeck().add("1_285");
        testDeck.getDrawDeck().add("1_291");
        // Personnel - Non-Aligned
        testDeck.getDrawDeck().add("1_320");
        testDeck.getDrawDeck().add("1_334");
        testDeck.getDrawDeck().add("1_351");
        // Ship - Federation
        testDeck.getDrawDeck().add("1_390");
        testDeck.getDrawDeck().add("1_390");
        testDeck.getDrawDeck().add("1_390");
        return testDeck;
    }
}
