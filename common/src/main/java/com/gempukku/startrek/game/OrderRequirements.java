package com.gempukku.startrek.game;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.game.ability.CardAbility;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.OrderAbility;
import com.gempukku.startrek.game.ability.OrderInterruptAbility;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.AndCardFilter;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.filter.OrCardFilter;
import com.gempukku.startrek.game.zone.CardInMissionComponent;

public class OrderRequirements {
    public static CardFilter createAttemptMissionRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem) {
        String filter = "type(Mission),owner(username(" + username + "))," +
                "not(missionType(Headquarters)),not(completedMission)," +
                "presentWith(type(Personnel),unstopped,matchesMissionAffiliations)";
        return cardFilteringSystem.resolveCardFilter(filter);
    }

    public static CardFilter createAttemptMissionShipsRequirements(
            Entity missionEntity,
            CardFilteringSystem cardFilteringSystem) {
        CardInMissionComponent mission = missionEntity.getComponent(CardInMissionComponent.class);
        String filter = "type(Ship),unstopped," +
                "inMission(username(" + mission.getMissionOwner() + ")," + mission.getMissionIndex() + ")";
        return cardFilteringSystem.resolveCardFilter(filter);
    }

    public static CardFilter createMissionAffiliationsShipRequirements(
            CardFilteringSystem cardFilteringSystem) {
        String filter = "hasOnBoard(type(Personnel),unstopped,matchesMissionAffiliations)";
        return cardFilteringSystem.resolveCardFilter(filter);
    }

    public static CardFilter createOrderRequirements(
            String username,
            CardFilteringSystem cardFilteringSystem) {
        CardFilter ownedInPlayFilter = cardFilteringSystem.resolveCardFilter(
                "or(zone(Core),zone(Mission)),owner(username(" + username + "))");
        CardFilter playableOrderCheckFilter = cardFilteringSystem.resolveCardFilter(
                "hasAbility(Order),orderConditionMatches");
        AndCardFilter orderInPlay = new AndCardFilter(ownedInPlayFilter, playableOrderCheckFilter);

        CardFilter ownerInHand = cardFilteringSystem.resolveCardFilter(
                "zone(Hand),owner(username(" + username + "))");
        CardFilter playableOrderInHandCheckFilter = cardFilteringSystem.resolveCardFilter(
                "hasAbility(OrderInterrupt),orderConditionMatches");
        AndCardFilter orderInHand = new AndCardFilter(ownerInHand, playableOrderInHandCheckFilter);

        return new OrCardFilter(orderInPlay, orderInHand);
    }

    public static int findUsableOrderIndex(
            Entity usedCardEntity, Memory memory,
            CardAbilitySystem cardAbilitySystem, ConditionResolverSystem conditionResolverSystem) {
        Array<CardAbility> cardAbilities = cardAbilitySystem.getCardAbilities(usedCardEntity);
        for (int i = 0; i < cardAbilities.size; i++) {
            CardAbility cardAbility = cardAbilities.get(i);
            if (cardAbility instanceof OrderAbility) {
                OrderAbility orderAbility = (OrderAbility) cardAbility;
                if (conditionResolverSystem.resolveBoolean(usedCardEntity, memory, orderAbility.getCondition()))
                    return i;
            }
            if (cardAbility instanceof OrderInterruptAbility) {
                OrderInterruptAbility orderInterruptAbility = (OrderInterruptAbility) cardAbility;
                if (conditionResolverSystem.resolveBoolean(usedCardEntity, memory, orderInterruptAbility.getCondition()))
                    return i;
            }
        }
        return -1;
    }
}
