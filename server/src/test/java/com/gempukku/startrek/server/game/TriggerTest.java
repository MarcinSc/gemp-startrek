package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.gempukku.startrek.decision.PlayerDecisionComponent;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.zone.CardZone;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TriggerTest extends AbstractGameTest {
    @Test
    public void testOptionalDrawCardPlayTriggerOnSelf() {
        setupGame(createDeckWithMissions("1_217"));

        putCardOnTopOfDeck("test1", "1_83");

        Entity card = getCardsInHand("test1").get(0);
        playCardSuccessfully(card);

        assertNotNull(card.getComponent(FaceDownCardInMissionComponent.class));

        // Pass on mandatory
        sendDecisionSuccessfully("test1", "action", "pass");
        sendDecisionSuccessfully("test2", "action", "pass");

        useTriggerSuccessfully(card, 0);

        assertEquals(1, getCardsInHand("test1").size);
    }

    @Test
    public void testOptionalDownloadCardPlayTriggerOnSelf() {
        setupGame(createDeckWithMissions("1_256"));

        Entity shipToDownload = putCardOnTopOfDeck("test1", "1_390");

        Entity card = getCardsInHand("test1").get(0);
        playCardSuccessfully(card);

        assertNotNull(card.getComponent(FaceDownCardInMissionComponent.class));

        // Pass on mandatory
        sendDecisionSuccessfully("test1", "action", "pass");
        sendDecisionSuccessfully("test2", "action", "pass");

        useTriggerSuccessfully(card, 0);

        PlayerDecisionComponent decision = getDecision();
        assertEquals("1_390", decision.getData().get("allCards"));
        assertEquals("1_390", decision.getData().get("matchingCards"));

        sendDecisionSuccessfully("test1", "selectedCards", "1_390");
        assertEquals(CardZone.Hand, shipToDownload.getComponent(CardComponent.class).getCardZone());
    }
}
