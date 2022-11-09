package com.gempukku.startrek.game;

import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;

public class TriggerRequirements {
    public static CardFilter createMandatoryTriggerRequirements(
            String username,
            String triggerType,
            String usedIds,
            CardFilteringSystem cardFilteringSystem,
            CardFilterResolverSystem cardFilterResolverSystem) {
        return cardFilterResolverSystem.resolveCardFilter(
                "or(zone(Mission),zone(Core)),owner(username(" + username + ")),hasAbility(Trigger)," +
                        "not(idIn(" + usedIds + "))" +
                        "triggerConditionMatches(" + triggerType + ",true)");
    }
}
