package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import org.junit.Test;

public class BeamTest extends AbstractGameTest {
    @Test
    public void beamToShip() {
        setupGame(createDeckWithMissions());

        Entity personnel = createCard("test1", "1_207");
        Entity ship = createCard("test1", "1_390");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        Entity headquartersMission = MissionOperations.findMission(world, "test1", 2);
        zoneOperations.moveCardToMission(personnel, headquartersMission, false);
        zoneOperations.moveCardToMission(ship, headquartersMission, true);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "beamFromMission",
                "shipId", getCardId(ship),
                "beamedId", getCardId(personnel));
    }
}
