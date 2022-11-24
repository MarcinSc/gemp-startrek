package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.OrderAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.AndCardFilter;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;

public class OrderRequirements {
    public static CardFilter createOrderRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem) {
        CardFilter ownedInPlayFilter = cardFilteringSystem.resolveCardFilter(
                "or(zone(Core),zone(Mission)),owner(username(" + username + "))");
        CardFilter playableOrderCheckFilter = cardFilteringSystem.resolveCardFilter(
                "hasAbility(Order),orderConditionMatches");
        return new AndCardFilter(ownedInPlayFilter, playableOrderCheckFilter);
    }

    public static int findUsableOrderIndex(
            Entity usedCardEntity, Memory memory,
            CardAbilitySystem cardAbilitySystem, ConditionResolverSystem conditionResolverSystem) {
        Array<CardAbility> cardAbilities = cardAbilitySystem.getCardAbilities(usedCardEntity);
        for (int i = 0; i < cardAbilities.size; i++) {
            CardAbility cardAbility = cardAbilities.get(i);
            if (cardAbility instanceof OrderAbility) {
                OrderAbility triggerAbility = (OrderAbility) cardAbility;
                if (conditionResolverSystem.resolveBoolean(usedCardEntity, memory, triggerAbility.getCondition()))
                    return i;
            }
        }
        return -1;
    }
}
