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
import com.gempukku.startrek.server.game.ability.DilemmaEffectHandler;
import com.gempukku.startrek.server.game.ability.TriggerAbilityHandler;
import com.gempukku.startrek.server.game.decision.DecisionSystem;
import com.gempukku.startrek.server.game.decision.PlayOrDrawDecisionHandler;
import com.gempukku.startrek.server.game.deck.PlayerDecklistComponent;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;
import com.gempukku.startrek.server.game.effect.card.DestroyEffect;
import com.gempukku.startrek.server.game.effect.card.PayCardCostEffect;
import com.gempukku.startrek.server.game.effect.control.*;
import com.gempukku.startrek.server.game.effect.deck.DrawCardEffect;
import com.gempukku.startrek.server.game.effect.deck.PlaceCardInHandOnBottomOfDeckEffect;
import com.gempukku.startrek.server.game.effect.deck.ShuffleDeckEffect;
import com.gempukku.startrek.server.game.effect.memory.MemorizeAmountEffect;
import com.gempukku.startrek.server.game.effect.play.PlayoutEventEffect;
import com.gempukku.startrek.server.game.effect.player.PlayerCounterEffect;
import com.gempukku.startrek.server.game.effect.setup.*;
import com.gempukku.startrek.server.game.effect.turn.SetTurnPlayerEffect;
import com.gempukku.startrek.server.game.effect.turn.SetTurnSegmentEffect;
import com.gempukku.startrek.server.game.effect.zone.MoveCardToMissionEffect;
import com.gempukku.startrek.server.game.effect.zone.MoveCardToZoneEffect;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;
import com.gempukku.startrek.server.game.stack.StackSystem;

import java.util.function.Consumer;

public class StarTrekGameHolder implements Disposable {
    private final World gameWorld;
    private final Entity gameEntity;

    private final StackSystem stackSystem;

    public StarTrekGameHolder(CardData cardData, boolean test) {
        gameWorld = createGameWorld(cardData, test);

        SpawnSystem spawnSystem = gameWorld.getSystem(SpawnSystem.class);
        spawnSystem.spawnEntity("game/executionStack.template");
        gameEntity = spawnSystem.spawnEntity("game/game.template");
        gameWorld.process();

        // Stack the game on the execution stack
        stackSystem = gameWorld.getSystem(StackSystem.class);
        stackSystem.stackEntity(gameEntity);
    }

    public World getGameWorld() {
        return gameWorld;
    }

    private static World createGameWorld(CardData cardDataService, boolean test) {
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        CommonGameWorldBuilder.createCommonSystems(worldConfigurationBuilder);
        worldConfigurationBuilder.with(
                // Base systems
                new SpawnSystem(),
                new EventSystem(new RuntimeEntityEventDispatcher()),

                // Specific systems
                new CardLookupSystem(cardDataService),
                new StackSystem(),
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
                new LoopEffect(),
                new RepeatEffect(),
                new SequenceEffect(),
                new StackActionEffect(),
                new ConditionEffect(),

                // Core game effects
                new PlayoutEventEffect(),

                // Specific game effects
                new PlayerCounterEffect(),
                new ShuffleDeckEffect(),
                new DecisionSystem(),
                new DrawCardEffect(),
                new PlaceCardInHandOnBottomOfDeckEffect(),
                new MoveCardToZoneEffect(),
                new MoveCardToMissionEffect(),
                new PayCardCostEffect(),
                new MemorizeAmountEffect(),
                new DestroyEffect(),

                // Ability handlers
                new TriggerAbilityHandler(),
                new DilemmaEffectHandler(),

                // Decision handlers
                new PlayOrDrawDecisionHandler(),

                // Network systems
                new RemoteEntityManagerHandler(),
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
        //setupTurnSequence();
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
