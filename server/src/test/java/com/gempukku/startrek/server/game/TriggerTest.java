package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import org.junit.Test;

public class TriggerTest extends AbstractGameTest {
    @Test
    public void testOptionalPlayTriggerOnSelf() {
        setupGame(createDeck("1_188", "1_217"));

        putCardOnTopOfDeck("test1", "1_83");

        Entity card = getCardsInHand("test1").get(0);
        playCardSuccessfully(card);

        // Pass on mandatory
        sendDecisionSuccessfully("test1", "action", "pass");
        sendDecisionSuccessfully("test2", "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "use",
                "cardId", String.valueOf(card.getId()),
                "triggerIndex", "0");
    }
}
