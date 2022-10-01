package com.gempukku.startrek.server.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.gempukku.libgdx.DummyApplication;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.hall.StarTrekDeck;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameSetupTest {
    private static CardData cardData;

    @BeforeClass
    public static void initialize() {
        Gdx.app = new DummyApplication();
        Gdx.files = new HeadlessFiles();
        cardData = new CardData();
        cardData.initializeCards();
    }

    @Test
    public void gameSetupTest() {
        StarTrekDeck testDeck = createTestDeck();
        StarTrekGameHolder gameHolder = new StarTrekGameHolder(cardData);
        gameHolder.addPlayer("test1", testDeck);
        gameHolder.addPlayer("test2", testDeck);
        gameHolder.processGame();
    }

    private StarTrekDeck createTestDeck() {
        StarTrekDeck testDeck = new StarTrekDeck();
        testDeck.setDeckId("deckId");
        testDeck.setDeckName("Example deck");

        testDeck.getMissions().add("12_40");
        testDeck.getMissions().add("12_42");
        testDeck.getMissions().add("29_36");
        testDeck.getMissions().add("7_45");
        testDeck.getMissions().add("30_32");

        testDeck.getDillemas().add("37_2");

        testDeck.getDrawDeck().add("12_58");
        return testDeck;
    }
}
