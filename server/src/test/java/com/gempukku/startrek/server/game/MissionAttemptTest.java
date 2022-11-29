package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.gempukku.startrek.decision.PlayerDecisionComponent;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        setupGame(createDeckWithMissions("1_4"));

        Entity personnel = createCard("test1", "1_207");
//        Entity dilemma = createCard("test2", "1_4");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity planetMission = missionOperations.findMission("test1", 4);
        zoneOperations.moveFromCurrentZoneToMission(personnel, planetMission, false);
//        zoneOperations.setupCardToTopOfDilemmaPile(dilemma, false);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "attemptPlanetMission",
                "missionId", getCardId(planetMission));

        sendDecisionSuccessfully("test2",
                "dilemmaStack", "1_4",
                "discardedDilemmas", "");
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
