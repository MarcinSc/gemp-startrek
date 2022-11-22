package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.zone.CardInCoreComponent;
import com.gempukku.startrek.game.zone.CardInDiscardComponent;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlayCardTest extends AbstractGameTest {
    @Test
    public void playPersonnelCard() {
        setupGame(createDeckWithMissions("1_210"));

        Entity playedCard = getCardsInHand("test1").get(0);
        playCardSuccessfully(playedCard);

        assertNotNull(playedCard.getComponent(FaceDownCardInMissionComponent.class));
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        MissionComponent mission = missionOperations.findMission("test1", 2).getComponent(MissionComponent.class);
        assertEquals(1, mission.getPlayerFaceDownCardsCount().get("test1").intValue());
    }

    @Test
    public void playShipCard() {
        setupGame(createDeckWithMissions("1_390"));

        Entity playedCard = getCardsInHand("test1").get(0);
        playCardSuccessfully(playedCard);

        assertNotNull(playedCard.getComponent(FaceUpCardInMissionComponent.class));
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        MissionComponent mission = missionOperations.findMission("test1", 2).getComponent(MissionComponent.class);
        assertEquals(0, mission.getPlayerFaceDownCardsCount().get("test1", 0).intValue());
    }

    @Test
    public void playCoreEventCard() {
        setupGame(createDeckWithMissions("1_83"));

        Entity playedCard = getCardsInHand("test1").get(0);
        playCardSuccessfully(playedCard);

        assertNotNull(playedCard.getComponent(CardInCoreComponent.class));
    }

    @Test
    public void playNonCoreEventCard() {
        setupGame(createDeckWithMissions("1_84", "1_84"));

        putCardOnTopOfDeck("test1", "1_83");
        putCardOnTopOfDeck("test1", "1_83");

        Entity playedCard = getCardsInHand("test1").get(0);
        playCardSuccessfully(playedCard);

        assertNotNull(playedCard.getComponent(CardInDiscardComponent.class));

        Array<Entity> cardsInHand = getCardsInHand("test1");
        assertEquals(1, cardsInHand.size);
        Entity cardInHand = cardsInHand.get(0);
        assertEquals("1_83", cardInHand.getComponent(CardComponent.class).getCardId());
    }
}
