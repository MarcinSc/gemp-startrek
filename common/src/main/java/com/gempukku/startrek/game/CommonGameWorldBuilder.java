package com.gempukku.startrek.game;

import com.artemis.WorldConfigurationBuilder;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.ability.HeadquarterRequirementsAbilityHandler;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.amount.CostToPlayAmountHandler;
import com.gempukku.startrek.game.amount.CounterCountAmountHandler;
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

                // Condition resolvers
                new ConditionResolverSystem(),
                new MemoryConditionHandler(),
                new CounterCountConditionHandler(),
                new DeckCountConditionHandler(),
                new LessOrEqualConditionHandler(),
                new HasCardConditionHandler(),

                // Card filter resovlers
                new CardFilterResolverSystem(),
                new CardTypeFilterHandler(),
                new CardIconFilterHandler(),
                new AffiliationFilterHandler(),
                new MissionTypeFilterHandler(),
                new OwnerFilterHandler(),
                new TitleFilterHandler(),
                new PlayableFilterHandler(),
                new ConditionFilterHandler(),
                new MemoryFilterHandler(),

                // Card abilities
                new CardAbilitySystem(),
                new HeadquarterRequirementsAbilityHandler(),

                // Other systems
                new CardFilteringSystem()
        );
    }
}
