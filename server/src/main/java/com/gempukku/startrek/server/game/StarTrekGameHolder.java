package com.gempukku.startrek.server.game;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.event.RuntimeEntityEventDispatcher;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.libgdx.network.server.RemoteEntityManagerHandler;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.CommonGameWorldBuilder;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.hall.StarTrekDeck;
import com.gempukku.startrek.server.common.NetworkEntityConfigurationSystem;
import com.gempukku.startrek.server.game.ability.*;
import com.gempukku.startrek.server.game.condition.MemoryMatchesHandler;
import com.gempukku.startrek.server.game.decision.*;
import com.gempukku.startrek.server.game.deck.PlayerDecklistComponent;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;
import com.gempukku.startrek.server.game.effect.beam.BeamBetweenShipsEffect;
import com.gempukku.startrek.server.game.effect.beam.BeamFromMissionEffect;
import com.gempukku.startrek.server.game.effect.beam.BeamToMissionEffect;
import com.gempukku.startrek.server.game.effect.card.DestroyEffect;
import com.gempukku.startrek.server.game.effect.card.ExecuteStopEffect;
import com.gempukku.startrek.server.game.effect.card.PayCardCostEffect;
import com.gempukku.startrek.server.game.effect.card.StopEffect;
import com.gempukku.startrek.server.game.effect.control.ConditionEffect;
import com.gempukku.startrek.server.game.effect.control.RepeatEffect;
import com.gempukku.startrek.server.game.effect.control.SequenceEffect;
import com.gempukku.startrek.server.game.effect.control.StackActionEffect;
import com.gempukku.startrek.server.game.effect.deck.DrawCardEffect;
import com.gempukku.startrek.server.game.effect.deck.PlaceCardInHandOnBottomOfDeckEffect;
import com.gempukku.startrek.server.game.effect.deck.ShuffleDeckEffect;
import com.gempukku.startrek.server.game.effect.memory.ClearMemoryEffect;
import com.gempukku.startrek.server.game.effect.memory.MemorizeAmountEffect;
import com.gempukku.startrek.server.game.effect.memory.RandomlySelectEffect;
import com.gempukku.startrek.server.game.effect.play.PlayoutEventEffect;
import com.gempukku.startrek.server.game.effect.play.PlayoutTriggerEffect;
import com.gempukku.startrek.server.game.effect.play.SetEffectStepEffect;
import com.gempukku.startrek.server.game.effect.player.PlayerCounterEffect;
import com.gempukku.startrek.server.game.effect.setup.*;
import com.gempukku.startrek.server.game.effect.turn.SetTurnPlayerEffect;
import com.gempukku.startrek.server.game.effect.turn.SetTurnSegmentEffect;
import com.gempukku.startrek.server.game.effect.zone.*;
import com.gempukku.startrek.server.game.filter.MemoryFilterHandler;
import com.gempukku.startrek.server.game.filter.ServerFacingDilemmaFilterHandler;
import com.gempukku.startrek.server.game.filter.ServerIdInFilterHandler;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;
import com.gempukku.startrek.server.game.stack.ObjectStackSystem;

import java.util.function.Consumer;

public class StarTrekGameHolder implements Disposable {
    private final World gameWorld;
    private final Entity gameEntity;

    private final ExecutionStackSystem stackSystem;

    public StarTrekGameHolder(CardData cardData, boolean test) {
        gameWorld = createGameWorld(cardData, test);

        SpawnSystem spawnSystem = gameWorld.getSystem(SpawnSystem.class);
        spawnSystem.spawnEntity("game/executionStack.template");
        gameEntity = spawnSystem.spawnEntity("game/game.template");
        gameWorld.process();

        // Stack the game on the execution stack
        stackSystem = gameWorld.getSystem(ExecutionStackSystem.class);
        stackSystem.stackEntity(gameEntity);
    }

    public World getGameWorld() {
        return gameWorld;
    }

    private static World createGameWorld(CardData cardDataService, boolean test) {
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        CommonGameWorldBuilder.createCommonSystems(worldConfigurationBuilder);
        ServerEntityIdSystem serverEntityIdSystem = new ServerEntityIdSystem();
        worldConfigurationBuilder.with(
                // Entity id
                serverEntityIdSystem,

                // Base systems
                new SpawnSystem(),
                new EventSystem(new RuntimeEntityEventDispatcher()),

                // Specific systems
                new CardLookupSystem(cardDataService),
                new ExecutionStackSystem(),
                new ObjectStackSystem(),
                new ZoneOperations(),

                // Setup effects
                new SetupMissionsEffect(),
                new SetupMissionCardsEffect(),
                new PlaceAllDilemmasInDeckEffect(),
                new PlaceAllCardsInDrawDeckEffect(),
                new SetupTurnOrderEffect(test),
                new SetTurnPlayerEffect(),
                new SetTurnSegmentEffect(),

                // Game effects
                new GameEffectSystem(),

                // Control game effects
                new RepeatEffect(),
                new SequenceEffect(),
                new StackActionEffect(),
                new ConditionEffect(),

                // Core game effects
                new PlayoutEventEffect(),
                new PlayoutTriggerEffect(),
                new SetEffectStepEffect(),

                // Specific game effects
                new PlayerCounterEffect(),
                new ShuffleDeckEffect(),
                new DecisionSystem(),
                new DrawCardEffect(),
                new PlaceCardInHandOnBottomOfDeckEffect(),
                new MoveCardToCoreEffect(),
                new MoveCardToDiscardPileEffect(),
                new MoveCardToMissionEffect(),
                new MoveCardToStackEffect(),
                new CreateEffectOnStackEffect(),
                new RemoveEffectFromStackEffect(),
                new PayCardCostEffect(),
                new ClearMemoryEffect(),
                new MemorizeAmountEffect(),
                new RandomlySelectEffect(),
                new DestroyEffect(),
                new StopEffect(),
                new ExecuteStopEffect(),

                new BeamFromMissionEffect(),
                new BeamToMissionEffect(),
                new BeamBetweenShipsEffect(),

                // Ability handlers
                new DilemmaEffectHandler(),
                new ServerEventAbilityHandler(),
                new ServerInterruptAbilityHandler(),
                new ServerTriggerAbilityHandler(),
                new ServerOrderAbilityHandler(),

                // Decision handlers
                new PlayOrDrawDecisionHandler(),
                new ExecuteOrdersDecisionHandler(),
                new MandatoryTriggerActionsDecisionHandler(),
                new OptionalTriggerActionsDecisionHandler(),

                // Server condition resolvers
                new MemoryMatchesHandler(),

                // Server card filters
                new MemoryFilterHandler(),
                new ServerIdInFilterHandler(),
                new ServerFacingDilemmaFilterHandler(),

                // Network systems
                new RemoteEntityManagerHandler(serverEntityIdSystem),
                new NetworkEntityConfigurationSystem());

        return new World(worldConfigurationBuilder.build());
    }

    @Override
    public void dispose() {
        gameWorld.dispose();
    }

    public void addPlayer(PlayerGameInfo playerGameInfo) {
        SpawnSystem spawnSystem = gameWorld.getSystem(SpawnSystem.class);
        Entity playerEntity = spawnSystem.spawnEntity("game/player.template");
        GamePlayerComponent player = playerEntity.getComponent(GamePlayerComponent.class);
        player.setName(playerGameInfo.getUsername());
        player.setDisplayName(playerGameInfo.getDisplayName());
        player.setAvatar(playerGameInfo.getAvatar());
        StarTrekDeck deck = playerGameInfo.getDeck();
        Array<String> cards = playerEntity.getComponent(PlayerDecklistComponent.class).getCards();
        cards.addAll(deck.getMissions());
        cards.addAll(deck.getDillemas());
        cards.addAll(deck.getDrawDeck());

        GameComponent game = gameEntity.getComponent(GameComponent.class);
        game.getPlayers().add(playerGameInfo.getUsername());
        EventSystem eventSystem = gameWorld.getSystem(EventSystem.class);
        eventSystem.fireEvent(EntityUpdated.instance, gameEntity);

        gameWorld.process();
    }

    public void setupGame() {
        LazyEntityUtil.forEachEntityWithComponent(gameWorld, GamePlayerComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        spawnPlayerCards(entity);
                    }
                });
    }

    private void spawnPlayerCards(Entity playerEntity) {
        CardLookupSystem cardLookupSystem = gameWorld.getSystem(CardLookupSystem.class);
        SpawnSystem spawnSystem = gameWorld.getSystem(SpawnSystem.class);

        GamePlayerComponent gamePlayer = playerEntity.getComponent(GamePlayerComponent.class);
        PlayerDecklistComponent decklist = playerEntity.getComponent(PlayerDecklistComponent.class);
        for (String cardId : decklist.getCards()) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
            if (cardDefinition == null)
                throw new RuntimeException("Unable to locate card with id: " + cardId);

            Entity cardEntity = spawnSystem.spawnEntity("game/card.template");
            CardComponent card = cardEntity.getComponent(CardComponent.class);
            card.setOwner(gamePlayer.getName());
            card.setCardId(cardId);
        }
    }

    public void processGame() {
        stackSystem.processStack();
    }
}
