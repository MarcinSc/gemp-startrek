package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import org.junit.Test;

public class MissionAttemptTest extends AbstractGameTest {
    @Test
    public void attemptPlanetMissionCorrectAffiliation() {
        setupGame(createDeckWithMissions());

        Entity personnel = createCard("test1", "1_207");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity planetMission = missionOperations.findMission("test1", 4);
        zoneOperations.moveFromCurrentZoneToMission(personnel, planetMission, false);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "attemptPlanetMission",
                "missionId", getCardId(planetMission));
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
