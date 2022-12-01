package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.gempukku.startrek.decision.PlayerDecisionComponent;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.zone.CardInPlayComponent;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import org.junit.Test;

import static org.junit.Assert.*;

public class MissionAttemptTest extends AbstractGameTest {
    @Test
    public void attemptPlanetMissionCorrectAffiliation() {
        setupGame(createDeckWithMissions());

        Entity personnel = createCard("test1", "1_207");
        Entity dilemma1 = createCard("test2", "1_4");
        Entity dilemma2 = createCard("test2", "1_8");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity planetMission = missionOperations.findMission("test1", 4);
        zoneOperations.moveFromCurrentZoneToMission(personnel, planetMission, false);
        zoneOperations.setupCardToTopOfDilemmaPile(dilemma2, false);
        zoneOperations.setupCardToTopOfDilemmaPile(dilemma1, false);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "attemptPlanetMission",
                "missionId", getCardId(planetMission));

        PlayerDecisionComponent decision = getDecision();
        assertEquals("test2", decision.getOwner());
        assertEquals("setupDilemmas", decision.getDecisionType());
        assertEquals("1", decision.getData().get("personnelCount"));
        assertEquals("1", decision.getData().get("costCount"));
        assertEquals("1_4", decision.getData().get("dilemmaCardIds"));
    }

    @Test
    public void processPlanetMissionAttempt() {
        setupGame(createDeckWithMissions("1_8", "1_4"));

        Entity personnel1 = createCard("test1", "1_207");
        Entity personnel2 = createCard("test1", "1_207");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity planetMission = missionOperations.findMission("test1", 4);
        zoneOperations.moveFromCurrentZoneToMission(personnel1, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(personnel2, planetMission, false);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "attemptPlanetMission",
                "missionId", getCardId(planetMission));

        sendDecisionSuccessfully("test2",
                "dilemmaStack", "1_8",
                "discardedDilemmas", "1_4");

        assertTrue(personnel1.getComponent(CardInPlayComponent.class).isStopped());
        assertTrue(personnel2.getComponent(CardInPlayComponent.class).isStopped());
        assertFalse(planetMission.getComponent(MissionComponent.class).isCompleted());
    }

    @Test
    public void processPlanetMissionAttemptSuccess() {
        setupGame(createDeckWithMissions());

        Entity shandor1 = createCard("test1", "1_225");
        Entity shandor2 = createCard("test1", "1_225");
        Entity mills1 = createCard("test1", "1_278");
        Entity mills2 = createCard("test1", "1_278");
        Entity jabara1 = createCard("test1", "1_214");
        Entity jabara2 = createCard("test1", "1_214");
        Entity jabara3 = createCard("test1", "1_214");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity planetMission = missionOperations.findMission("test1", 4);
        zoneOperations.moveFromCurrentZoneToMission(shandor1, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(shandor2, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(mills1, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(mills2, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(jabara1, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(jabara2, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(jabara3, planetMission, false);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "attemptPlanetMission",
                "missionId", getCardId(planetMission));

        sendDecisionSuccessfully("test2",
                "dilemmaStack", "",
                "discardedDilemmas", "");

        assertEquals(35, getPlayer("test1").getComponent(PlayerPublicStatsComponent.class).getPointCount());
        assertTrue(planetMission.getComponent(MissionComponent.class).isCompleted());
    }

    @Test
    public void processPlanetMissionAttemptNotMatchingRequirements() {
        setupGame(createDeckWithMissions());

        Entity shandor1 = createCard("test1", "1_225");
        Entity shandor2 = createCard("test1", "1_225");
        Entity mills1 = createCard("test1", "1_278");
        Entity mills2 = createCard("test1", "1_278");
        Entity jabara1 = createCard("test1", "1_214");
        Entity jabara2 = createCard("test1", "1_214");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity planetMission = missionOperations.findMission("test1", 4);
        zoneOperations.moveFromCurrentZoneToMission(shandor1, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(shandor2, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(mills1, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(mills2, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(jabara1, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(jabara2, planetMission, false);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "attemptPlanetMission",
                "missionId", getCardId(planetMission));

        sendDecisionSuccessfully("test2",
                "dilemmaStack", "",
                "discardedDilemmas", "");

        assertTrue(shandor1.getComponent(CardInPlayComponent.class).isStopped());
        assertTrue(shandor2.getComponent(CardInPlayComponent.class).isStopped());
        assertTrue(mills1.getComponent(CardInPlayComponent.class).isStopped());
        assertTrue(mills2.getComponent(CardInPlayComponent.class).isStopped());
        assertTrue(jabara1.getComponent(CardInPlayComponent.class).isStopped());
        assertTrue(jabara2.getComponent(CardInPlayComponent.class).isStopped());
        assertEquals(0, getPlayer("test1").getComponent(PlayerPublicStatsComponent.class).getPointCount());
        assertFalse(planetMission.getComponent(MissionComponent.class).isCompleted());
    }

    @Test
    public void processPlanetMissionAttemptTooExpensive() {
        setupGame(createDeckWithMissions());

        Entity personnel1 = createCard("test1", "1_207");
        Entity personnel2 = createCard("test1", "1_207");
        Entity dilemmaCard1 = createCard("test2", "1_8");
        Entity dilemmaCard2 = createCard("test2", "1_4");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity planetMission = missionOperations.findMission("test1", 4);
        zoneOperations.moveFromCurrentZoneToMission(personnel1, planetMission, false);
        zoneOperations.moveFromCurrentZoneToMission(personnel2, planetMission, false);
        zoneOperations.setupCardToTopOfDilemmaPile(dilemmaCard1, false);
        zoneOperations.setupCardToTopOfDilemmaPile(dilemmaCard2, false);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "attemptPlanetMission",
                "missionId", getCardId(planetMission));

        sendDecisionSuccessfully("test2",
                "dilemmaStack", "1_8,1_4",
                "discardedDilemmas", "");

        assertEquals(getCardId(planetMission), dilemmaCard1.getComponent(CardInPlayComponent.class).getAttachedToId());
        assertEquals(getCardId(planetMission), dilemmaCard2.getComponent(CardInPlayComponent.class).getAttachedToId());
        assertFalse(planetMission.getComponent(MissionComponent.class).isCompleted());
    }

    @Test
    public void attemptPlanetMissionInvalidAffiliation() {
        setupGame(createDeckWithMissions());

        Entity personnel = createCard("test1", "1_265");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity planetMission = missionOperations.findMission("test1", 4);
        zoneOperations.moveFromCurrentZoneToMission(personnel, planetMission, false);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionFailure("test1",
                "action", "attemptPlanetMission",
                "missionId", getCardId(planetMission));
    }
}
