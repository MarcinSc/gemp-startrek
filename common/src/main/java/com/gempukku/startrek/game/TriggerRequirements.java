package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.TriggerAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class TriggerRequirements {
    public static CardFilter createMandatoryTriggerRequirements(
            String username,
            String triggerType,
            CardFilteringSystem cardFilteringSystem) {
        return cardFilteringSystem.resolveCardFilter(
                "or(zone(Mission),zone(Core)),owner(username(" + username + ")),hasAbility(Trigger)," +
                        "not(memory(userIds))," +
                        "triggerConditionMatches(" + triggerType + ",false)");
    }

    public static CardFilter createOptionalTriggerRequirements(
            String username,
            String triggerType,
            CardFilteringSystem cardFilteringSystem) {
        return cardFilteringSystem.resolveCardFilter(
                "or(zone(Mission),zone(Core)),owner(username(" + username + ")),hasAbility(Trigger)," +
                        "not(memory(userIds))," +
                        "triggerConditionMatches(" + triggerType + ",true)");
    }

    public static int findUsableTriggerIndex(
            Entity usedCardEntity, String triggerType, boolean optional, Memory memory,
            CardAbilitySystem cardAbilitySystem, ConditionResolverSystem conditionResolverSystem) {
        Array<CardAbility> cardAbilities = cardAbilitySystem.getCardAbilities(usedCardEntity);
        for (int i = 0; i < cardAbilities.size; i++) {
            CardAbility cardAbility = cardAbilities.get(i);
            if (cardAbility instanceof TriggerAbility) {
                TriggerAbility triggerAbility = (TriggerAbility) cardAbility;
                if (triggerAbility.getTriggerType().equals(triggerType) && triggerAbility.isOptional() == optional) {
                    if (conditionResolverSystem.resolveBoolean(usedCardEntity, memory, triggerAbility.getCondition()))
                        return i;
                }
            }
        }
        return -1;
    }
}
