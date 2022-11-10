package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.TriggerRequirements;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.stack.StackSystem;

public class MandatoryTriggerActionsDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;
    private CardFilteringSystem cardFilteringSystem;
    private CardFilterResolverSystem cardFilterResolverSystem;
    private StackSystem stackSystem;

    @Override
    protected void initialize() {
        decisionSystem.registerDecisionTypeHandler("mandatoryTriggerActions", this);
    }

    @Override
    public boolean validateDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        try {
            String action = result.get("action");
            if (action == null)
                return false;

            if (action.equals("pass") && canPass(decisionPlayer, decisionData)) {
                return true;
            } else if (action.equals("use")) {
                int cardId = Integer.parseInt(result.get("cardId"));
                Entity usedCardEntity = world.getEntity(cardId);
                if (usedCardEntity == null)
                    return false;


                Entity sourceEntity = null;
                String sourceIdStr = decisionData.get("sourceId");
                if (sourceIdStr != null)
                    sourceEntity = world.getEntity(Integer.parseInt(sourceIdStr));

                String usedIds = decisionData.get("usedIds", "");

                CardFilter triggerFilter = TriggerRequirements.createMandatoryTriggerRequirements(
                        decisionPlayer, decisionData.get("triggerType"), usedIds,
                        cardFilterResolverSystem);
                if (triggerFilter.accepts(sourceEntity, new Memory(decisionData), usedCardEntity))
                    return true;
            }
        } catch (Exception exp) {
            // Ignore
        }
        return false;
    }

    private boolean canPass(String username, ObjectMap<String, String> decisionData) {
        Entity sourceEntity = null;
        String sourceIdStr = decisionData.get("sourceId");
        if (sourceIdStr != null)
            sourceEntity = world.getEntity(Integer.parseInt(sourceIdStr));

        String usedIds = decisionData.get("usedIds", "");

        CardFilter triggerFilter = TriggerRequirements.createMandatoryTriggerRequirements(
                username, decisionData.get("triggerType"), usedIds,
                cardFilterResolverSystem);
        return cardFilteringSystem.cantFindCard(sourceEntity, new Memory(decisionData), triggerFilter);
    }

    @Override
    public void processDecision(String decisionPlayer, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        String action = result.get("action");
        if (action.equals("pass")) {
            Entity memoryEntity = stackSystem.getTopMostStackEntityWithComponent(EffectMemoryComponent.class);
            ObjectMap<String, String> memory = memoryEntity.getComponent(EffectMemoryComponent.class).getMemory();
            int playersPassed = Integer.parseInt(memory.get("playersPassed", "0"));
            memory.put("playersPassed", String.valueOf(playersPassed + 1));
        } else if (action.equals("use")) {
            // Execute the use action
            int cardId = Integer.parseInt(result.get("cardId"));

            // TODO stack the action

            Entity memoryEntity = stackSystem.getTopMostStackEntityWithComponent(EffectMemoryComponent.class);
            ObjectMap<String, String> memory = memoryEntity.getComponent(EffectMemoryComponent.class).getMemory();
            memory.put("playersPassed", "0");
            memory.put("usedIds", appendToList(memory.get("usedIds"), String.valueOf(cardId)));
        }
    }

    private String appendToList(String source, String appended) {
        if (source == null)
            return appended;
        return source + "," + appended;
    }

    @Override
    protected void processSystem() {

    }
}
