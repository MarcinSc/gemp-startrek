package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.DummyApplication;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.decision.DecisionMade;
import com.gempukku.startrek.decision.PlayerDecisionComponent;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.server.game.stack.StackSystem;
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

        StackSystem stackSystem = gameHolder.getGameWorld().getSystem(StackSystem.class);
        EventSystem eventSystem = gameHolder.getGameWorld().getSystem(EventSystem.class);

        Entity decisionEntity = stackSystem.getTopMostStackEntity();
        PlayerDecisionComponent decision = decisionEntity.getComponent(PlayerDecisionComponent.class);

        ObjectMap<String, String> decisionParams = new ObjectMap<>();

        DecisionMade decisionMade = new DecisionMade(decisionParams);
        decisionMade.setOrigin(decision.getOwner());

        eventSystem.fireEvent(decisionMade, decisionEntity);
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
