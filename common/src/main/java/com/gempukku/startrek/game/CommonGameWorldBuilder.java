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
                new CostToPlayAmountHandler(),
                new HandCountAmountHandler(),
                new MemoryAmountHandler(),

                // Condition resolvers
                new ConditionResolverSystem(),
                new MemoryConditionHandler(),
                new CounterCountConditionHandler(),
                new DeckCountConditionHandler(),
                new LessOrEqualConditionHandler(),
                new HasCardConditionHandler(),
                new MemoryMatchesHandler(),

                // Card filter resovlers
                new CardFilterResolverSystem(),
                new CardTypeFilterHandler(),
                new CardIconFilterHandler(),
                new AffiliationFilterHandler(),
                new SkillFilterHandler(),
                new MissionTypeFilterHandler(),
                new OwnerFilterHandler(),
                new YourFilterHandler(),
                new TitleFilterHandler(),
                new UniquenessPreservedFilterHandler(),
                new PlayableFilterHandler(),
                new ConditionForMatchedFilterHandler(),
                new MemoryFilterHandler(),
                new HasAbilityFilterHandler(),

                // Card abilities
                new CardAbilitySystem(),
                new PlaysInCoreAbilityHandler(),
                new OrderAbilityHandler(),
                new EventAbilityHandler(),
                new InterruptAbilityHandler(),
                new HeadquarterRequirementsAbilityHandler(),
                new GrantSkillAbilityHandler(),

                // Other systems
                new CardFilteringSystem()
        );
    }
}
