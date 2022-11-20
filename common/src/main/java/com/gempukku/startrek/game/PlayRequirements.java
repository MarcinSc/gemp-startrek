package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.HeadquarterRequirements;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.AndCardFilter;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.filter.OrCardFilter;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;

public class PlayRequirements {
    public static CardFilter createBeamShipRequirements(
            String username,
            CardFilterResolverSystem cardFilterResolverSystem) {
        return cardFilterResolverSystem.resolveCardFilter("type(Ship),unstopped,owner(username(" + username + "))");
    }

    public static CardFilter createBeamSelectAnotherShipRequirements(
            String username,
            Entity shipEntity,
            CardFilterResolverSystem cardFilterResolverSystem) {
        FaceUpCardInMissionComponent ship = shipEntity.getComponent(FaceUpCardInMissionComponent.class);
        String missionOwner = ship.getMissionOwner();
        int missionIndex = ship.getMissionIndex();
        CardFilter shipFilter = cardFilterResolverSystem.resolveCardFilter("type(Ship),unstopped," +
                "onMission(username(" + missionOwner + ")," + missionIndex + ")," +
                "owner(username(" + username + "))");

        Array<CardFilter> resultFilters = new Array<>();
        // Not same ship
        resultFilters.add(
                new CardFilter() {
                    @Override
                    public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                        return cardEntity != shipEntity;
                    }
                });
        resultFilters.add(shipFilter);

        return new AndCardFilter(resultFilters);
    }

    public static CardFilter createBeamFromMissionRequirements(
            String username,
            Entity missionEntity,
            Entity shipEntity,
            CardFilterResolverSystem cardFilterResolverSystem) {
        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
        String missionOwner = mission.getOwner();
        int missionIndex = mission.getMissionIndex();
        return cardFilterResolverSystem.resolveCardFilter(
                "or(type(Personnel),type(Equipment)),unstopped," +
                        "onMission(username(" + missionOwner + ")," + missionIndex + ")," +
                        "notAboardShip," +
                        "owner(username(" + username + "))");
    }

    // Remember to also check if the card is aboard the ship
    public static CardFilter createBeamToMissionRequirements(
            String username,
            Entity missionEntity,
            Entity shipEntity,
            CardFilterResolverSystem cardFilterResolverSystem) {
        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
        String missionOwner = mission.getOwner();
        int missionIndex = mission.getMissionIndex();
        return cardFilterResolverSystem.resolveCardFilter(
                "or(type(Personnel),type(Equipment)),unstopped," +
                        "onMission(username(" + missionOwner + ")," + missionIndex + ")," +
                        "owner(username(" + username + "))");
    }

    // Remember to also check if the card is aboard the ship
    public static CardFilter createBeamBetweenShipsRequirements(
            String username,
            Entity fromShipEntity,
            Entity toShipEntity,
            CardFilterResolverSystem cardFilterResolverSystem) {
        FaceUpCardInMissionComponent ship = fromShipEntity.getComponent(FaceUpCardInMissionComponent.class);
        String missionOwner = ship.getMissionOwner();
        int missionIndex = ship.getMissionIndex();
        return cardFilterResolverSystem.resolveCardFilter(
                "or(type(Personnel),type(Equipment)),unstopped," +
                        "onMission(username(" + missionOwner + ")," + missionIndex + ")," +
                        "owner(username(" + username + "))");
    }

    public static CardFilter createPlayRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem,
            CardFilterResolverSystem cardFilterResolverSystem,
            CardAbilitySystem cardAbilitySystem) {
        OrCardFilter playabilityFilter = playabilityCheckFilter(username, cardFilteringSystem, cardFilterResolverSystem, cardAbilitySystem);
        CardFilter handOwnedFilter = cardFilterResolverSystem.resolveCardFilter("zone(Hand),owner(username(" + username + "))");

        Array<CardFilter> filters = new Array<>();
        filters.add(handOwnedFilter);
        filters.add(playabilityFilter);

        return new AndCardFilter(filters);
    }

    private static OrCardFilter playabilityCheckFilter(String username, CardFilteringSystem cardFilteringSystem, CardFilterResolverSystem cardFilterResolverSystem, CardAbilitySystem cardAbilitySystem) {
        CardFilter eventFilter = createEventPlayRequirements(username, cardFilterResolverSystem);
        CardFilter nonEventFilter = createNonEventPlayRequirements(
                username, cardFilteringSystem, cardFilterResolverSystem, cardAbilitySystem);

        Array<CardFilter> filters = new Array<>();
        filters.add(eventFilter, nonEventFilter);

        return new OrCardFilter(filters);
    }

    private static CardFilter createNonEventPlayRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem,
            CardFilterResolverSystem cardFilterResolverSystem,
            CardAbilitySystem cardAbilitySystem) {
        Entity playerHeadquarter = cardFilteringSystem.findFirstCardInPlay("missionType(Headquarters),owner(username(" + username + "))");
        CardFilter headquarterRequirements = cardAbilitySystem.getCardAbilities(playerHeadquarter, HeadquarterRequirements.class).get(0).getCardFilter();
        CardFilter nonEventCards = cardFilterResolverSystem.resolveCardFilter(
                "or(type(Personnel),type(Ship),type(Equipment)),"
                        + "uniquenessPreserved,playable,"
                        + "conditionForMatched(lessOrEqual(costToPlay,counterCount(username(" + username + "))))");
        Array<CardFilter> filters = new Array<>();
        filters.add(headquarterRequirements, nonEventCards);

        return new AndCardFilter(filters);
    }

    private static CardFilter createEventPlayRequirements(
            String username,
            CardFilterResolverSystem cardFilterResolverSystem) {
        return cardFilterResolverSystem.resolveCardFilter(
                "type(Event),playable," +
                        "conditionForMatched(lessOrEqual(costToPlay,counterCount(username(" + username + "))))");
    }
}
