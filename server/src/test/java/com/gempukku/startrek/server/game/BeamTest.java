package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.zone.CardInPlayComponent;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BeamTest extends AbstractGameTest {
    @Test
    public void beamToShip() {
        setupGame(createDeckWithMissions());

        Entity personnel = createCard("test1", "1_207");
        Entity ship = createCard("test1", "1_390");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        Entity headquartersMission = MissionOperations.findMission(world, "test1", 2);
        zoneOperations.moveFromCurrentZoneToMission(personnel, headquartersMission, false);
        zoneOperations.moveFromCurrentZoneToMission(ship, headquartersMission, true);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "beamFromMission",
                "shipId", getCardId(ship),
                "beamedId", getCardId(personnel));

        assertEquals(getCardId(ship), personnel.getComponent(CardInPlayComponent.class).getAttachedToId());
        MissionComponent mission = MissionOperations.findMission(world, "test1", 2).getComponent(MissionComponent.class);
        assertEquals(0, mission.getPlayerFaceDownCardsCount().get("test1", 0).intValue());
        assertEquals(1, ship.getComponent(CardInPlayComponent.class).getAttachedFaceDownCount());
    }

    @Test
    public void beamToMission() {
        setupGame(createDeckWithMissions());

        Entity personnel = createCard("test1", "1_207");
        Entity ship = createCard("test1", "1_390");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        Entity headquartersMission = MissionOperations.findMission(world, "test1", 2);
        zoneOperations.moveFromCurrentZoneToMission(personnel, headquartersMission, false);
        zoneOperations.moveFromCurrentZoneToMission(ship, headquartersMission, true);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "beamFromMission",
                "shipId", getCardId(ship),
                "beamedId", getCardId(personnel));

        sendDecisionSuccessfully("test1",
                "action", "beamToMission",
                "shipId", getCardId(ship),
                "beamedId", getCardId(personnel));

        assertNull(personnel.getComponent(CardInPlayComponent.class).getAttachedToId());
        MissionComponent mission = MissionOperations.findMission(world, "test1", 2).getComponent(MissionComponent.class);
        assertEquals(1, mission.getPlayerFaceDownCardsCount().get("test1", 0).intValue());
        assertEquals(0, ship.getComponent(CardInPlayComponent.class).getAttachedFaceDownCount());
    }

    @Test
    public void beamBetweenShips() {
        setupGame(createDeckWithMissions());

        Entity personnel = createCard("test1", "1_207");
        Entity ship1 = createCard("test1", "1_390");
        Entity ship2 = createCard("test1", "1_390");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        Entity headquartersMission = MissionOperations.findMission(world, "test1", 2);
        zoneOperations.moveFromCurrentZoneToMission(personnel, headquartersMission, false);
        zoneOperations.moveFromCurrentZoneToMission(ship1, headquartersMission, true);
        zoneOperations.moveFromCurrentZoneToMission(ship2, headquartersMission, true);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "beamFromMission",
                "shipId", getCardId(ship1),
                "beamedId", getCardId(personnel));

        sendDecisionSuccessfully("test1",
                "action", "beamBetweenShips",
                "fromShipId", getCardId(ship1),
                "toShipId", getCardId(ship2),
                "beamedId", getCardId(personnel));

        assertEquals(getCardId(ship2), personnel.getComponent(CardInPlayComponent.class).getAttachedToId());
        MissionComponent mission = MissionOperations.findMission(world, "test1", 2).getComponent(MissionComponent.class);
        assertEquals(0, mission.getPlayerFaceDownCardsCount().get("test1", 0).intValue());
        assertEquals(0, ship1.getComponent(CardInPlayComponent.class).getAttachedFaceDownCount());
        assertEquals(1, ship2.getComponent(CardInPlayComponent.class).getAttachedFaceDownCount());
    }
    // TODO add negative tests
}
