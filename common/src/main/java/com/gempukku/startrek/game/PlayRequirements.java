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

public class PlayRequirements {
    public static CardFilter createPlayRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem,
            CardFilterResolverSystem cardFilterResolverSystem,
            CardAbilitySystem cardAbilitySystem) {
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
        CardFilter headquarterRequirements = cardAbilitySystem.getCardAbility(playerHeadquarter, HeadquarterRequirements.class).getCardFilter();
        CardFilter nonEventCards = cardFilterResolverSystem.resolveCardFilter(
                "or(type(Personnel),type(Ship),type(Equipment)),"
                        + "uniquenessPreserved,playable,"
                        + "condition(lessOrEqual(costToPlay,counterCount(username(" + username + "))))");
        Array<CardFilter> filters = new Array<>();
        filters.add(headquarterRequirements, nonEventCards);

        return new AndCardFilter(filters);
    }

    private static CardFilter createEventPlayRequirements(
            String username,
            CardFilterResolverSystem cardFilterResolverSystem) {
        return cardFilterResolverSystem.resolveCardFilter(
                "type(Event),playable," +
                        "condition(lessOrEqual(costToPlay,counterCount(username(" + username + "))))");
    }
}
