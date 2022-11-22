package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.zone.CardInMissionComponent;
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
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity headquartersMission = missionOperations.findMission("test1", 2);
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
        MissionComponent mission = missionOperations.findMission("test1", 2).getComponent(MissionComponent.class);
        assertEquals(0, mission.getPlayerFaceDownCardsCount().get("test1", 0).intValue());
        assertEquals(1, ship.getComponent(CardInPlayComponent.class).getAttachedFaceDownCount());
    }

    @Test
    public void beamToMission() {
        setupGame(createDeckWithMissions());

        Entity personnel = createCard("test1", "1_207");
        Entity ship = createCard("test1", "1_390");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity headquartersMission = missionOperations.findMission("test1", 2);
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
        MissionComponent mission = missionOperations.findMission("test1", 2).getComponent(MissionComponent.class);
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
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity headquartersMission = missionOperations.findMission("test1", 2);
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
        MissionComponent mission = missionOperations.findMission("test1", 2).getComponent(MissionComponent.class);
        assertEquals(0, mission.getPlayerFaceDownCardsCount().get("test1", 0).intValue());
        assertEquals(0, ship1.getComponent(CardInPlayComponent.class).getAttachedFaceDownCount());
        assertEquals(1, ship2.getComponent(CardInPlayComponent.class).getAttachedFaceDownCount());
    }

    @Test
    public void moveShip() {
        setupGame(createDeckWithMissions());

        Entity personnel1 = createCard("test1", "1_251");
        Entity personnel2 = createCard("test1", "1_251");
        Entity personnel3 = createCard("test1", "1_251");
        Entity ship = createCard("test1", "1_393");

        ZoneOperations zoneOperations = world.getSystem(ZoneOperations.class);
        MissionOperations missionOperations = world.getSystem(MissionOperations.class);
        Entity headquartersMission = missionOperations.findMission("test1", 2);
        zoneOperations.moveFromCurrentZoneToMission(personnel1, headquartersMission, false);
        zoneOperations.moveFromCurrentZoneToMission(personnel2, headquartersMission, false);
        zoneOperations.moveFromCurrentZoneToMission(personnel3, headquartersMission, false);
        zoneOperations.moveFromCurrentZoneToMission(ship, headquartersMission, true);

        zoneOperations.attachToShip(ship, personnel1);
        zoneOperations.attachToShip(ship, personnel2);
        zoneOperations.attachToShip(ship, personnel3);

        // Pass the play or draw
        sendDecisionSuccessfully("test1",
                "action", "pass");

        sendDecisionSuccessfully("test1",
                "action", "moveShip",
                "shipId", getCardId(ship),
                "missionId", getCardId(findEntity("type(Mission),inMission(username(test1),0)")));

        assertEquals(0, ship.getComponent(CardInMissionComponent.class).getMissionIndex());
        assertEquals(0, personnel1.getComponent(CardInMissionComponent.class).getMissionIndex());
        assertEquals(0, personnel2.getComponent(CardInMissionComponent.class).getMissionIndex());
        assertEquals(0, personnel3.getComponent(CardInMissionComponent.class).getMissionIndex());
    }
    // TODO add negative tests
}
