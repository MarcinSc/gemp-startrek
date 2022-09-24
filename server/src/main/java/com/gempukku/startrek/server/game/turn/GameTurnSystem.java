package com.gempukku.startrek.server.game.turn;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.GameComponent;
import com.gempukku.startrek.game.turn.TurnComponent;
import com.gempukku.startrek.game.turn.TurnSegment;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import com.gempukku.startrek.server.game.ExecuteStackedAction;
import com.gempukku.startrek.server.game.ExecutionStackComponent;

public class GameTurnSystem extends BaseSystem {
    private ServerSpawnSystem serverSpawnSystem;
    private EventSystem eventSystem;

    private Entity executionStackEntity;

    @EventListener
    public void executeGameAction(ExecuteStackedAction action, Entity entity) {
        GameComponent game = entity.getComponent(GameComponent.class);
        if (game != null) {
            Entity turnSequenceEntity = serverSpawnSystem.spawnEntity("game/turnSequence.template");
            Array<String> players = game.getPlayers();
            TurnSequenceComponent turnSequence = turnSequenceEntity.getComponent(TurnSequenceComponent.class);
            turnSequence.getPlayers().addAll(players);

            stackExecutionEntity(entity);
        }
    }

    @EventListener
    public void executeTurnSequenceAction(ExecuteStackedAction action, Entity entity) {
        TurnSequenceComponent turnSequence = entity.getComponent(TurnSequenceComponent.class);
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
            Entity turnEntity = serverSpawnSystem.spawnEntity("game/turn.template");
            TurnComponent turn = turnEntity.getComponent(TurnComponent.class);
            turn.setPlayer(nextPlayerTurn);
        }
    }

    @EventListener
    public void executeTurnAction(ExecuteStackedAction action, Entity entity) {
        TurnComponent turn = entity.getComponent(TurnComponent.class);
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
                removeExecutionEntity(entity);
            } else {
                turn.setTurnSegment(nextTurnSegment);
                eventSystem.fireEvent(EntityUpdated.instance, entity);

                Entity turnSegmentEntity = serverSpawnSystem.spawnEntity(nextTurnSegment.getEntityTemplate());
                stackExecutionEntity(turnSegmentEntity);
            }
        }
    }

    private void stackExecutionEntity(Entity entity) {
        executionStackEntity.getComponent(ExecutionStackComponent.class).getEntityIds().add(entity.getId());
        // Not needed on client - no need to replicate changes
        // eventSystem.fireEvent(EntityUpdated.instance, executionStackEntity);
    }

    private void removeExecutionEntity(Entity entity) {
        Array<Integer> entityIds = executionStackEntity.getComponent(ExecutionStackComponent.class).getEntityIds();
        entityIds.removeIndex(entityIds.size - 1);
        world.deleteEntity(entity);
    }

    private Entity getExecutionStackEntity() {
        if (executionStackEntity == null) {
            executionStackEntity = LazyEntityUtil.findEntityWithComponent(world, ExecutionStackComponent.class);
        }
        return executionStackEntity;
    }

    @Override
    protected void processSystem() {

    }
}
