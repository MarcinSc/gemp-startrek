package com.gempukku.startrek.game;

import com.artemis.WorldConfigurationBuilder;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.ability.*;
import com.gempukku.startrek.game.amount.*;
import com.gempukku.startrek.game.condition.*;
import com.gempukku.startrek.game.filter.*;
import com.gempukku.startrek.game.filter.source.*;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class CommonGameWorldBuilder {
    public static void createCommonSystems(WorldConfigurationBuilder worldConfigurationBuilder) {
        worldConfigurationBuilder.with(
                new GameEntityProvider(),
                new MissionOperations(),

                new ExpressionSystem(),

                // Player resolvers
                new PlayerResolverSystem(),

                // Amount resolvers
                new AmountResolverSystem(),
                new MinusAmountHandler(),
                new CardMatchingCount(),
                new CounterCountAmountHandler(),
                new HandCountAmountHandler(),
                new DeckCountAmountHandler(),
                new CostToPlayAmountHandler(),
                new AttributeAmountHandler(),
                new MissionPointsAmountHandler(),
                new ShipRangeAmountHandler(),
                new SkillCountHandler(),
                new MemoryAmountHandler(),
                new PlayerCountAmountHandler(),

                // Condition resolvers
                new ConditionResolverSystem(),
                new MatchesConditionHandler(),
                new MemoryConditionHandler(),
                new EqualsConditionHandler(),
                new LessOrEqualConditionHandler(),
                new AtLeastConditionHandler(),
                new CountAtLeastConditionHandler(),
                new CountSkillAtLeastConditionHandler(),
                new CountMemorySkillAtLeastConditionHandler(),
                new HasCardConditionHandler(),
                new CanBeginEngagementInvolvingPersonnelConditionHandler(),

                // Card filter handlers
                new MemoryFilterHandler(),
                new IdInFilterHandler(),
                new AttachedToFilterHandler(),
                new CardTypeFilterHandler(),
                new CardIconFilterHandler(),
                new CardZoneFilterHandler(),
                new AffiliationFilterHandler(),
                new SkillFilterHandler(),
                new MemorySkillFilterHandler(),
                new StaffedFilterHandler(),
                new InRangeFilterHandler(),
                new MissionTypeFilterHandler(),
                new MissionMatchesFilterHandler(),
                new QuadrantFilterHandler(),
                new OwnerFilterHandler(),
                new YourFilterHandler(),
                new UnstoppedFilterHandler(),
                new InMissionFilterHandler(),
                new PresentWithFilter(),
                new NotAboardShipFilterHandler(),
                new TitleFilterHandler(),
                new UniquenessPreservedFilterHandler(),
                new PlayableFilterHandler(),
                new ConditionForMatchedFilterHandler(),
                new HasAbilityFilterHandler(),
                new TriggerConditionMatchesFilterHandler(),
                new OrderConditionMatchesFilterHandler(),
                new HasOnBoardFilterHandler(),
                new MatchesMissionAffiliationsFilterHandler(),
                new IsInMissionFilterHandler(),
                new CompletedMissionFilterHandler(),
                new AttemptingPersonnelFilter(),

                // Card source handlers
                new AnySource(),
                new SelfSource(),
                new MissionSource(),
                new InPlaySource(),
                new HandSource(),
                new MemorySource(),
                new AttemptingPersonnelSource(),

                // Card abilities
                new CardAbilitySystem(),
                new PlaysInCoreAbilityHandler(),
                new HeadquarterRequirementsAbilityHandler(),
                new GrantSkillAbilityHandler(),
                new MoveCostModifierAbilityHandler(),

                // Other systems
                new CardFilteringSystem()
        );
    }
}
