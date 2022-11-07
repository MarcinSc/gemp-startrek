package com.gempukku.startrek.server.game.decision;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.decision.DecisionMade;
import com.gempukku.startrek.decision.PlayerDecisionComponent;
import com.gempukku.startrek.game.GamePlayerComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.stack.ExecuteStackedAction;
import com.gempukku.startrek.server.game.stack.StackSystem;

public class DecisionSystem extends EffectSystem {
    private StackSystem stackSystem;
    private PlayerResolverSystem playerResolverSystem;
    private ComponentMapper<PlayerDecisionComponent> playerDecisionComponentMapper;

    private ObjectMap<String, DecisionTypeHandler> decisionTypeHandlerMap = new ObjectMap<>();

    public DecisionSystem() {
        super("sendDecisionToPlayer");
    }

    public void registerDecisionTypeHandler(String decisionType, DecisionTypeHandler decisionTypeHandler) {
        decisionTypeHandlerMap.put(decisionType, decisionTypeHandler);
    }

    @EventListener
    public void decisionMade(DecisionMade decisionMade, Entity entity) {
        PlayerDecisionComponent playerDecision = stackSystem.getTopMostStackEntity().getComponent(PlayerDecisionComponent.class);
        if (playerDecision != null && playerDecision.getOwner().equals(decisionMade.getOrigin())) {
            if (validateDecision(playerDecision, decisionMade.getParameters())) {
                Entity decisionEntity = stackSystem.removeTopStackEntity();
                world.deleteEntity(decisionEntity);
                processDecisionAnswer(playerDecision, decisionMade.getParameters());
            } else {
                // Invalid decision was made - next processing will re-create the decision
                Entity decisionEntity = stackSystem.removeTopStackEntity();
                world.deleteEntity(decisionEntity);
            }
        }
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
    public boolean processEndingEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        String player = gameEffect.getData().getString("player");
        Entity playerEntity = playerResolverSystem.resolvePlayer(gameEffectEntity, gameEffect.getMemory(), player);

        Entity decisionEntity = world.createEntity();
        PlayerDecisionComponent decision = playerDecisionComponentMapper.create(decisionEntity);
        decision.setOwner(playerEntity.getComponent(GamePlayerComponent.class).getName());
        decision.setDecisionType(gameEffect.getData().getString("decisionType"));
        decision.setData(gameEffect.getData().get("data"));

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
    protected void processEffect(Entity gameEffectEntity, GameEffectComponent gameEffect) {
        // Ignore
    }
}
