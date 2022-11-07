package com.gempukku.startrek.game;

import com.artemis.WorldConfigurationBuilder;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.condition.CounterCountConditionHandler;
import com.gempukku.startrek.game.condition.DeckCountConditionHandler;
import com.gempukku.startrek.game.condition.MemoryConditionHandler;
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

                // Condition resolvers
                new ConditionResolverSystem(),
                new MemoryConditionHandler(),
                new CounterCountConditionHandler(),
                new DeckCountConditionHandler(),

                // Card filter resovlers
                new CardFilterResolverSystem(),
                new CardTypeFilterHandler(),
                new CardIconFilterHandler(),
                new MissionTypeFilterHandler(),
                new OwnerFilterHandler(),

                // Other systems
                new CardFilteringSystem()
        );
    }
}
