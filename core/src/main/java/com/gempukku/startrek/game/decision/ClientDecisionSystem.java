package com.gempukku.startrek.game.decision;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.decision.DecisionMade;
import com.gempukku.startrek.decision.PlayerDecisionComponent;

public class ClientDecisionSystem extends BaseEntitySystem {
    private EventSystem eventSystem;
    private boolean processingDecision;
    private Entity decisionEntityToProcess;
    private ObjectMap<String, DecisionHandler> decisionHandlerMap = new ObjectMap<>();

    public ClientDecisionSystem() {
        super(Aspect.all(PlayerDecisionComponent.class));
    }

    @Override
    protected void inserted(int entityId) {
        decisionEntityToProcess = world.getEntity(entityId);
    }

    public void registerDecisionHandler(String decisionType, DecisionHandler decisionHandler) {
        decisionHandlerMap.put(decisionType, decisionHandler);
    }

    public void executeDecision(ObjectMap<String, String> parameters) {
        if (processingDecision) {
            Entity decisionEntity = LazyEntityUtil.findEntityWithComponent(world, PlayerDecisionComponent.class);
            eventSystem.fireEvent(new DecisionMade(parameters), decisionEntity);

            processingDecision = false;
        }
    }

    @Override
    protected void processSystem() {
        if (decisionEntityToProcess != null) {
            PlayerDecisionComponent playerDecision = decisionEntityToProcess.getComponent(PlayerDecisionComponent.class);
            String decisionType = playerDecision.getDecisionType();
            DecisionHandler decisionHandler = decisionHandlerMap.get(decisionType);
            if (decisionHandler == null)
                throw new GdxRuntimeException("Unable to find decision handler for decision type: " + decisionType);
            processingDecision = true;
            decisionEntityToProcess = null;

            decisionHandler.processNewDecision(playerDecision.getData());
        }
    }
}
