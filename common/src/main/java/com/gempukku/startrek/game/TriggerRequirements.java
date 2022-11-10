package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.TriggerAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;

public class TriggerRequirements {
    public static CardFilter createMandatoryTriggerRequirements(
            String username,
            String triggerType,
            String usedIds,
            CardFilterResolverSystem cardFilterResolverSystem) {
        if (usedIds == null)
            usedIds = "";
        return cardFilterResolverSystem.resolveCardFilter(
                "or(zone(Mission),zone(Core)),owner(username(" + username + ")),hasAbility(Trigger)," +
                        "not(idIn(" + usedIds + "))," +
                        "triggerConditionMatches(" + triggerType + ",false)");
    }

    public static CardFilter createOptionalTriggerRequirements(
            String username,
            String triggerType,
            String usedIds,
            CardFilterResolverSystem cardFilterResolverSystem) {
        if (usedIds == null)
            usedIds = "";
        return cardFilterResolverSystem.resolveCardFilter(
                "or(zone(Mission),zone(Core)),owner(username(" + username + ")),hasAbility(Trigger)," +
                        "not(idIn(" + usedIds + "))," +
                        "triggerConditionMatches(" + triggerType + ",true)");
    }

    public static int findUsableTriggerIndex(
            Entity usedCardEntity, String triggerType, boolean optional, Memory memory,
            CardAbilitySystem cardAbilitySystem, ConditionResolverSystem conditionResolverSystem) {
        Array<TriggerAbility> triggerAbilities = cardAbilitySystem.getCardAbilities(usedCardEntity, TriggerAbility.class);
        for (int i = 0; i < triggerAbilities.size; i++) {
            TriggerAbility triggerAbility = triggerAbilities.get(i);
            if (triggerAbility.getTriggerType().equals(triggerType) && triggerAbility.isOptional() == optional) {
                if (conditionResolverSystem.resolveBoolean(usedCardEntity, memory, triggerAbility.getCondition()))
                    return i;
            }
        }
        return -1;
    }
}
