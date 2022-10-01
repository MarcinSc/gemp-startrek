package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.event.RuntimeEntityEventDispatcher;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.libgdx.network.server.RemoteEntityManagerHandler;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.server.common.NetworkEntityConfigurationSystem;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import com.gempukku.startrek.server.game.amount.AmountResolverSystem;
import com.gempukku.startrek.server.game.card.CardLookupSystem;
import com.gempukku.startrek.server.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.server.game.condition.MemoryConditionHandler;
import com.gempukku.startrek.server.game.decision.DecisionEffect;
import com.gempukku.startrek.server.game.deck.PlayerDecklistComponent;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;
import com.gempukku.startrek.server.game.effect.LoopEffect;
import com.gempukku.startrek.server.game.effect.PlayerCounterEffect;
import com.gempukku.startrek.server.game.effect.StackActionEffect;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.turn.GameTurnSystem;
import com.gempukku.startrek.server.game.turn.PlayAndDrawSegmentSystem;

public class StarTrekGameHolder implements Disposable {
    private final World gameWorld;
    private final Entity executionStackEntity;
    private final Entity gameEntity;

    public StarTrekGameHolder(CardData cardData) {
        gameWorld = createGameWorld(cardData);

        ServerSpawnSystem spawnSystem = gameWorld.getSystem(ServerSpawnSystem.class);
        executionStackEntity = spawnSystem.spawnEntity("game/executionStack.template");
        gameEntity = spawnSystem.spawnEntity("game/game.template");

        // Stack the game on the execution stack
        executionStackEntity.getComponent(ExecutionStackComponent.class).getEntityIds().add(gameEntity.getId());
    }

    public World getGameWorld() {
        return gameWorld;
    }

    private static World createGameWorld(CardData cardDataService) {
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        worldConfigurationBuilder.with(
                // Base systems
                new ServerSpawnSystem(),
                new EventSystem(new RuntimeEntityEventDispatcher()),

                // Specific systems
                new CardLookupSystem(cardDataService),
                new GameTurnSystem(),
                new PlayAndDrawSegmentSystem(),
                new ExpressionSystem(),

                // Game effects
                new GameEffectSystem(),
                new StackActionEffect(),
                new PlayerCounterEffect(),
                new LoopEffect(),
                new DecisionEffect(),

                // Resolvers
                new PlayerResolverSystem(),
                new AmountResolverSystem(),

                new ConditionResolverSystem(),
                new MemoryConditionHandler(),

                // Network systems
                new RemoteEntityManagerHandler(),
                new NetworkEntityConfigurationSystem());

        return new World(worldConfigurationBuilder.build());
    }

    @Override
    public void dispose() {
        gameWorld.dispose();
    }

    public void addPlayer(String username, StarTrekDeck deck) {
        ServerSpawnSystem spawnSystem = gameWorld.getSystem(ServerSpawnSystem.class);
        Entity playerEntity = spawnSystem.spawnEntity("game/player.template");
        GamePlayerComponent player = playerEntity.getComponent(GamePlayerComponent.class);
        player.setName(username);
        Array<String> cards = playerEntity.getComponent(PlayerDecklistComponent.class).getCards();
        cards.addAll(deck.getMissions());
        cards.addAll(deck.getDillemas());
        cards.addAll(deck.getDrawDeck());

        GameComponent game = gameEntity.getComponent(GameComponent.class);
        game.getPlayers().add(username);
        EventSystem eventSystem = gameWorld.getSystem(EventSystem.class);
        eventSystem.fireEvent(EntityUpdated.instance, gameEntity);
    }

    public void processGame() {
        EventSystem eventSystem = gameWorld.getSystem(EventSystem.class);

        ExecuteStackedAction executeStackedAction = new ExecuteStackedAction();
        do {
            ExecutionStackComponent executionStack = executionStackEntity.getComponent(ExecutionStackComponent.class);
            Array<Integer> entityIds = executionStack.getEntityIds();
            Entity topMostStackEntity = gameWorld.getEntity(entityIds.get(entityIds.size - 1));
            eventSystem.fireEvent(executeStackedAction, topMostStackEntity);
            gameWorld.process();
        } while (!executeStackedAction.isFinishedProcessing());
    }
}
