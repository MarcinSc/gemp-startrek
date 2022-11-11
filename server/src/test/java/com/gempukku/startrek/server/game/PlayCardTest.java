package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.zone.CardInCoreComponent;
import com.gempukku.startrek.game.zone.CardInDiscardComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayCardTest extends AbstractGameTest {
    @Test
    public void playPersonnelCard() {
        setupGame(createDeck("1_188", "1_210"));

        Entity playedCard = getCardsInHand("test1").get(0);
        assertTrue(
                sendDecision("test1",
                        "action", "play",
                        "cardId", String.valueOf(playedCard.getId())));

        assertNotNull(playedCard.getComponent(FaceUpCardInMissionComponent.class));
    }

    @Test
    public void playShipCard() {
        setupGame(createDeck("1_188", "1_390"));

        Entity playedCard = getCardsInHand("test1").get(0);
        assertTrue(
                sendDecision("test1",
                        "action", "play",
                        "cardId", String.valueOf(playedCard.getId())));

        assertNotNull(playedCard.getComponent(FaceUpCardInMissionComponent.class));
    }

    @Test
    public void playCoreEventCard() {
        setupGame(createDeck("1_188", "1_83"));

        Entity playedCard = getCardsInHand("test1").get(0);
        assertTrue(
                sendDecision("test1",
                        "action", "play",
                        "cardId", String.valueOf(playedCard.getId())));

        assertNotNull(playedCard.getComponent(CardInCoreComponent.class));
    }

    @Test
    public void playNonCoreEventCard() {
        setupGame(createDeck("1_188", "1_84", "1_84"));

        putCardOnTopOfDeck("test1", "1_83");
        putCardOnTopOfDeck("test1", "1_83");

        Entity playedCard = getCardsInHand("test1").get(0);
        assertTrue(
                sendDecision("test1",
                        "action", "play",
                        "cardId", String.valueOf(playedCard.getId())));

        assertNotNull(playedCard.getComponent(CardInDiscardComponent.class));

        Array<Entity> cardsInHand = getCardsInHand("test1");
        assertEquals(1, cardsInHand.size);
        Entity cardInHand = cardsInHand.get(0);
        assertEquals("1_83", cardInHand.getComponent(CardComponent.class).getCardId());
    }
}
