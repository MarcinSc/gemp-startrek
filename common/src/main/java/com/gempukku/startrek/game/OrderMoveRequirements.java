package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.gempukku.startrek.game.filter.AndCardFilter;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class OrderMoveRequirements {
    public static CardFilter createMoveShipRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem) {
        return cardFilteringSystem.resolveCardFilter(
                "zone(Mission),type(Ship),unstopped,owner(username(" + username + ")),staffed");
    }

    public static CardFilter createMoveShipMissionRequirements(
            String username,
            Entity shipEntity,
            CardFilteringSystem cardFilteringSystem) {
        CardInMissionComponent cardInMission = shipEntity.getComponent(CardInMissionComponent.class);
        String missionOwner = cardInMission.getMissionOwner();
        int missionIndex = cardInMission.getMissionIndex();
        return cardFilteringSystem.resolveCardFilter("zone(Mission),type(Mission),not(inMission(username(" + missionOwner + ")," + missionIndex + ")),inRange");
    }

    public static CardFilter createBeamFromMissionShipRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem) {
        return cardFilteringSystem.resolveCardFilter("zone(Mission),type(Ship),unstopped,owner(username(" + username + "))");
    }

    public static CardFilter createBeamToMissionShipRequirements(String username, CardFilteringSystem cardFilteringSystem) {
        return cardFilteringSystem.resolveCardFilter("zone(Mission),type(Ship),unstopped,owner(username(" + username + ")),missionMatches(or(missionType(Planet),missionType(Headquarters)))");
    }

    public static CardFilter createBeamSelectAnotherShipRequirements(
            String username,
            Entity shipEntity,
            CardFilteringSystem cardFilteringSystem) {
        CardInMissionComponent ship = shipEntity.getComponent(CardInMissionComponent.class);
        String missionOwner = ship.getMissionOwner();
        int missionIndex = ship.getMissionIndex();
        CardFilter shipFilter = cardFilteringSystem.resolveCardFilter("zone(Mission),type(Ship),unstopped," +
                "inMission(username(" + missionOwner + ")," + missionIndex + ")," +
                "owner(username(" + username + "))");

        // Not same ship
        CardFilter notSameShip =
                new CardFilter() {
                    @Override
                    public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                        return cardEntity != shipEntity;
                    }
                };

        return new AndCardFilter(notSameShip, shipFilter);
    }

    public static CardFilter createBeamFromMissionRequirements(
            String username,
            Entity shipEntity,
            CardFilteringSystem cardFilteringSystem) {
        CardInMissionComponent cardInMission = shipEntity.getComponent(CardInMissionComponent.class);
        String missionOwner = cardInMission.getMissionOwner();
        int missionIndex = cardInMission.getMissionIndex();
        return cardFilteringSystem.resolveCardFilter(
                "zone(Mission),or(type(Personnel),type(Equipment)),unstopped," +
                        "inMission(username(" + missionOwner + ")," + missionIndex + ")," +
                        "notAboardShip," +
                        "owner(username(" + username + "))");
    }

    // Remember to also check if the card is aboard the ship
    public static CardFilter createBeamToMissionRequirements(
            String username,
            Entity shipEntity,
            CardFilteringSystem cardFilteringSystem) {
        CardInMissionComponent cardInMission = shipEntity.getComponent(CardInMissionComponent.class);
        String missionOwner = cardInMission.getMissionOwner();
        int missionIndex = cardInMission.getMissionIndex();
        return cardFilteringSystem.resolveCardFilter(
                "zone(Mission),or(type(Personnel),type(Equipment)),unstopped," +
                        "inMission(username(" + missionOwner + ")," + missionIndex + ")," +
                        "owner(username(" + username + "))");
    }

    // Remember to also check if the card is aboard the ship
    public static CardFilter createBeamBetweenShipsRequirements(
            String username,
            Entity fromShipEntity,
            Entity toShipEntity,
            CardFilteringSystem cardFilteringSystem) {
        CardInMissionComponent ship = fromShipEntity.getComponent(CardInMissionComponent.class);
        String missionOwner = ship.getMissionOwner();
        int missionIndex = ship.getMissionIndex();
        return cardFilteringSystem.resolveCardFilter(
                "zone(Mission),or(type(Personnel),type(Equipment)),unstopped," +
                        "inMission(username(" + missionOwner + ")," + missionIndex + ")," +
                        "owner(username(" + username + "))");
    }

}
