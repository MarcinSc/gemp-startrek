package com.gempukku.startrek.game;

import com.artemis.WorldConfigurationBuilder;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.ability.*;
import com.gempukku.startrek.game.amount.*;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.condition.*;
import com.gempukku.startrek.game.filter.*;
import com.gempukku.startrek.game.player.PlayerResolverSystem;

public class CommonGameWorldBuilder {
    public static void createCommonSystems(WorldConfigurationBuilder worldConfigurationBuilder) {
        worldConfigurationBuilder.with(
                new ExpressionSystem(),

                // Player resolvers
                new PlayerResolverSystem(),

                // Amount resolvers
                new AmountResolverSystem(),
                new CounterCountAmountHandler(),
                new HandCountAmountHandler(),
                new DeckCountAmountHandler(),
                new CostToPlayAmountHandler(),
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
                new HasCardConditionHandler(),

                // Card filter resovlers
                new CardFilterResolverSystem(),
                new CardTypeFilterHandler(),
                new CardIconFilterHandler(),
                new CardZoneFilterHandler(),
                new AffiliationFilterHandler(),
                new SkillFilterHandler(),
                new MissionTypeFilterHandler(),
                new MissionMatchesFilterHandler(),
                new QuadrantFilterHandler(),
                new OwnerFilterHandler(),
                new YourFilterHandler(),
                new UnstoppedFilterHandler(),
                new OnMissionFilterHandler(),
                new NotAboardShipFilterHandler(),
                new TitleFilterHandler(),
                new UniquenessPreservedFilterHandler(),
                new PlayableFilterHandler(),
                new ConditionForMatchedFilterHandler(),
                new HasAbilityFilterHandler(),
                new TriggerConditionMatchesFilterHandler(),

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
