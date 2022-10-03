package com.gempukku.startrek.server.game.turn;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.turn.TurnComponent;
import com.gempukku.startrek.game.turn.TurnSegment;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import com.gempukku.startrek.server.game.card.CardLookupSystem;
import com.gempukku.startrek.server.game.stack.ExecuteStackedAction;
import com.gempukku.startrek.server.game.stack.StackSystem;

public class GameTurnSystem extends BaseSystem {
    private ServerSpawnSystem spawnSystem;
    private EventSystem eventSystem;
    private CardLookupSystem cardLookupSystem;
    private StackSystem stackSystem;

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
