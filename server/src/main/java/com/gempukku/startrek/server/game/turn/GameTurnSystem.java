package com.gempukku.startrek.server.game.turn;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.game.turn.TurnComponent;
import com.gempukku.startrek.game.turn.TurnSegment;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import com.gempukku.startrek.server.game.card.CardComponent;
import com.gempukku.startrek.server.game.card.CardLookupSystem;
import com.gempukku.startrek.server.game.card.CardZone;
import com.gempukku.startrek.server.game.deck.PlayerDeckComponent;
import com.gempukku.startrek.server.game.deck.PlayerDecklistComponent;
import com.gempukku.startrek.server.game.deck.PlayerDilemmaPileComponent;
import com.gempukku.startrek.server.game.stack.ExecuteStackedAction;
import com.gempukku.startrek.server.game.stack.StackSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GameTurnSystem extends BaseSystem {
    private ServerSpawnSystem spawnSystem;
    private EventSystem eventSystem;
    private CardLookupSystem cardLookupSystem;
    private StackSystem stackSystem;

    @EventListener
    public void executeGameAction(ExecuteStackedAction action, Entity gameEntity) {
        GameComponent game = gameEntity.getComponent(GameComponent.class);
        if (game != null) {
            setupGame(gameEntity);

            setupTurnSequence(game);
        }
    }

    private void setupGame(Entity entity) {
        LazyEntityUtil.forEachEntityWithComponent(world, GamePlayerComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        spawnPlayerCards(entity);
                    }
                });
    }

    private void spawnPlayerCards(Entity playerEntity) {
        GamePlayerComponent gamePlayer = playerEntity.getComponent(GamePlayerComponent.class);
        PlayerDecklistComponent decklist = playerEntity.getComponent(PlayerDecklistComponent.class);
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        for (String cardId : decklist.getCards()) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
            if (cardDefinition == null)
                throw new RuntimeException("Unable to locate card with id: " + cardId);

            Entity cardEntity = spawnSystem.spawnEntity("game/card.template");
            CardComponent card = cardEntity.getComponent(CardComponent.class);
            card.setOwner(gamePlayer.getName());
            card.setCardId(cardId);
            CardType cardType = cardDefinition.getType();
            if (cardType == CardType.Dilemma) {
                card.setCardZone(CardZone.DILLEMMA_PILE);
                dilemmaPile.getCards().add(cardEntity.getId());
            } else if (cardType == CardType.Mission) {
                card.setCardZone(CardZone.MISSIONS);
            } else {
                card.setCardZone(CardZone.DECK);
                deck.getCards().add(cardEntity.getId());
            }
        }

        deck.getCards().shuffle();
        dilemmaPile.getCards().shuffle();
    }

    private void setupTurnSequence(GameComponent game) {
        Entity turnSequenceEntity = spawnSystem.spawnEntity("game/turnSequence.template");
        Array<String> players = game.getPlayers();
        List<String> orderedPlayers = determinePlayerOrder(players);

        TurnSequenceComponent turnSequence = turnSequenceEntity.getComponent(TurnSequenceComponent.class);
        Array<String> turnPlayers = turnSequence.getPlayers();
        for (String orderedPlayer : orderedPlayers) {
            turnPlayers.add(orderedPlayer);
        }

        stackSystem.stackEntity(turnSequenceEntity);
    }

    private List<String> determinePlayerOrder(Array<String> players) {
        List<String> orderedPlayers = new ArrayList<>();
        for (String player : players) {
            orderedPlayers.add(player);
        }

        Collections.shuffle(orderedPlayers);
        return orderedPlayers;
    }

    @EventListener
    public void executeTurnSequenceAction(ExecuteStackedAction action, Entity turnSequenceEntity) {
        TurnSequenceComponent turnSequence = turnSequenceEntity.getComponent(TurnSequenceComponent.class);
        if (turnSequence != null) {
            Array<String> players = turnSequence.getPlayers();
            String nextPlayerTurn;

            String lastPlayerTurn = turnSequence.getLastPlayerTurn();
            if (lastPlayerTurn == null) {
                nextPlayerTurn = players.first();
            } else {
                int nextPlayerIndex = (players.indexOf(lastPlayerTurn, false) + 1) % players.size;
                nextPlayerTurn = players.get(nextPlayerIndex);
            }
            Entity turnEntity = spawnSystem.spawnEntity("game/turn.template");
            TurnComponent turn = turnEntity.getComponent(TurnComponent.class);
            turn.setPlayer(nextPlayerTurn);

            stackSystem.stackEntity(turnEntity);
        }
    }

    @EventListener
    public void executeTurnAction(ExecuteStackedAction action, Entity turnEntity) {
        TurnComponent turn = turnEntity.getComponent(TurnComponent.class);
        if (turn != null) {
            TurnSegment nextTurnSegment;

            TurnSegment lastTurnSegment = turn.getTurnSegment();
            if (lastTurnSegment == null) {
                nextTurnSegment = TurnSegment.PLAY_AND_DRAW_CARDS;
            } else if (lastTurnSegment == TurnSegment.PLAY_AND_DRAW_CARDS) {
                nextTurnSegment = TurnSegment.EXECUTE_ORDERS;
            } else if (lastTurnSegment == TurnSegment.EXECUTE_ORDERS) {
                nextTurnSegment = TurnSegment.DISCARD_EXCESS_CARDS;
            } else {
                nextTurnSegment = null;
            }

            if (nextTurnSegment == null) {
                world.deleteEntity(stackSystem.removeTopStackEntity());
            } else {
                turn.setTurnSegment(nextTurnSegment);
                eventSystem.fireEvent(EntityUpdated.instance, turnEntity);

                Entity turnSegmentEntity = spawnSystem.spawnEntity(nextTurnSegment.getEntityTemplate());
                stackSystem.stackEntity(turnSegmentEntity);
            }
        }
    }

    @Override
    protected void processSystem() {

    }
}
