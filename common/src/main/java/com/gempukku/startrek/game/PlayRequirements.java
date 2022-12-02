package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.HeadquarterRequirements;
import com.gempukku.startrek.game.filter.AndCardFilter;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.filter.OrCardFilter;

public class PlayRequirements {

    public static CardFilter createPlayRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem,
            CardAbilitySystem cardAbilitySystem) {
        CardFilter handOwnedFilter = cardFilteringSystem.resolveCardFilter("zone(Hand),owner(username(" + username + "))");
        OrCardFilter playabilityFilter = playabilityCheckFilter(username, cardFilteringSystem, cardAbilitySystem);

        return new AndCardFilter(handOwnedFilter, playabilityFilter);
    }

    private static OrCardFilter playabilityCheckFilter(String username, CardFilteringSystem cardFilteringSystem, CardAbilitySystem cardAbilitySystem) {
        CardFilter eventFilter = createEventPlayRequirements(username, cardFilteringSystem);
        CardFilter nonEventFilter = createNonEventPlayRequirements(
                username, cardFilteringSystem, cardAbilitySystem);

        Array<CardFilter> filters = new Array<>();
        filters.add(eventFilter, nonEventFilter);

        return new OrCardFilter(filters);
    }

    private static CardFilter createNonEventPlayRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem,
            CardAbilitySystem cardAbilitySystem) {
        Entity playerHeadquarter = cardFilteringSystem.findFirstCard(null, null, "inPlay", "missionType(Headquarters),owner(username(" + username + "))");
        CardFilter headquarterRequirements = cardAbilitySystem.getCardAbilities(playerHeadquarter, HeadquarterRequirements.class).get(0).getCardFilter();
        CardFilter nonEventCards = cardFilteringSystem.resolveCardFilter(
                "or(type(Personnel),type(Ship),type(Equipment)),"
                        + "uniquenessPreserved,playable,"
                        + "conditionForMatched(lessOrEqual(costToPlay(self),counterCount(username(" + username + "))))");

        return new AndCardFilter(headquarterRequirements, nonEventCards);
    }

    private static CardFilter createEventPlayRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem) {
        return cardFilteringSystem.resolveCardFilter(
                "type(Event),playable," +
                        "conditionForMatched(lessOrEqual(costToPlay(self),counterCount(username(" + username + "))))");
    }
}
