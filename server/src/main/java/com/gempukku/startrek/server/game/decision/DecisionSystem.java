package com.gempukku.startrek.server.game.decision;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.decision.DecisionMade;
import com.gempukku.startrek.decision.PlayerDecisionComponent;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.stack.ExecuteStackedAction;
import com.gempukku.startrek.server.game.stack.StackSystem;

public class DecisionSystem extends EffectSystem {
    private StackSystem stackSystem;
    private PlayerResolverSystem playerResolverSystem;
    private ComponentMapper<PlayerDecisionComponent> playerDecisionComponentMapper;

    private ObjectMap<String, DecisionTypeHandler> decisionTypeHandlerMap = new ObjectMap<>();

    public DecisionSystem() {
        super("processDecision");
    }

    public void registerDecisionTypeHandler(String decisionType, DecisionTypeHandler decisionTypeHandler) {
        decisionTypeHandlerMap.put(decisionType, decisionTypeHandler);
    }

    @EventListener
    public boolean decisionMade(DecisionMade decisionMade, Entity entity) {
        PlayerDecisionComponent playerDecision = stackSystem.getTopMostStackEntity().getComponent(PlayerDecisionComponent.class);
        if (playerDecision != null && playerDecision.getOwner().equals(decisionMade.getOrigin())) {
            if (validateDecision(playerDecision, decisionMade.getParameters())) {
                // Process and remove decision entity
                Entity decisionEntity = stackSystem.removeTopStackEntity();
                Entity sendDecisionEntity = stackSystem.removeTopStackEntity();
                processDecisionAnswer(playerDecision, decisionMade.getParameters());
                world.deleteEntity(decisionEntity);
                world.deleteEntity(sendDecisionEntity);
                return true;
            } else {
                // Invalid decision was made - next processing will re-create the decision
                Entity decisionEntity = stackSystem.removeTopStackEntity();
                world.deleteEntity(decisionEntity);
            }
        }
        return false;
    }

    @EventListener
    public void bounceOnStackedDecision(ExecuteStackedAction action, Entity entity) {
        PlayerDecisionComponent playerDecision = entity.getComponent(PlayerDecisionComponent.class);
        if (playerDecision != null) {
            // For some reason we have hit a decision, we are waiting for answer from player now
            action.setFinishedProcessing(true);
        }
    }

    @Override
    public boolean processEndingEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        String player = gameEffect.getDataString("player");
        Entity playerEntity = playerResolverSystem.resolvePlayer(sourceEntity, memory, player);

        Entity decisionEntity = world.createEntity();
        PlayerDecisionComponent decision = playerDecisionComponentMapper.create(decisionEntity);
        decision.setOwner(playerEntity.getComponent(GamePlayerComponent.class).getName());
        decision.setDecisionType(gameEffect.getDataString("decisionType"));

        JsonValue memoryData = gameEffect.getClonedDataObject("memoryData");
        if (memoryData != null) {
            ObjectMap<String, String> data = decision.getData();
            for (JsonValue memoryDatum : memoryData) {
                String name = memoryDatum.name();
                String memoryName = memoryDatum.asString();
                String value = memory.getValue(memoryName);
                data.put(name, value);
            }
        }

        JsonValue dataJson = gameEffect.getClonedDataObject("data");
        if (dataJson != null) {
            ObjectMap<String, String> data = decision.getData();
            for (JsonValue jsonValue : dataJson) {
                String name = jsonValue.name();
                String value = jsonValue.asString();
                data.put(name, value);
            }
        }

        stackEffect(decisionEntity);

        return true;
    }

    private boolean validateDecision(PlayerDecisionComponent playerDecision, ObjectMap<String, String> parameters) {
        String decisionType = playerDecision.getDecisionType();
        DecisionTypeHandler decisionTypeHandler = decisionTypeHandlerMap.get(decisionType);
        if (decisionTypeHandler == null)
            throw new RuntimeException("Unable to locate DecisionTypeHandler for type: " + decisionType);

        return decisionTypeHandler.validateDecision(playerDecision.getOwner(), playerDecision.getData(), parameters);
    }

    private void processDecisionAnswer(PlayerDecisionComponent playerDecision, ObjectMap<String, String> parameters) {
        String decisionType = playerDecision.getDecisionType();
        DecisionTypeHandler decisionTypeHandler = decisionTypeHandlerMap.get(decisionType);
        if (decisionTypeHandler == null)
            throw new RuntimeException("Unable to locate DecisionTypeHandler for type: " + decisionType);

        decisionTypeHandler.processDecision(playerDecision.getOwner(), playerDecision.getData(), parameters);
    }

    @Override
    protected void processEffect(Entity sourceEntity, GameEffectComponent gameEffect, Memory memory) {
        // Ignore
    }
}
