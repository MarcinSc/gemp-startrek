package com.gempukku.startrek.server.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.TriggerRequirements;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.server.game.effect.EffectMemoryComponent;
import com.gempukku.startrek.server.game.stack.ExecutionStackSystem;

public class MandatoryTriggerActionsDecisionHandler extends BaseSystem implements DecisionTypeHandler {
    private DecisionSystem decisionSystem;
    private CardFilteringSystem cardFilteringSystem;
    private ExecutionStackSystem stackSystem;
    private SpawnSystem spawnSystem;
    private CardAbilitySystem cardAbilitySystem;
    private ConditionResolverSystem conditionResolverSystem;
    private ServerEntityIdSystem serverEntityIdSystem;

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
                Entity usedCardEntity = serverEntityIdSystem.findfromId(result.get("cardId"));
                if (usedCardEntity == null)
                    return false;

                int triggerIndex = Integer.parseInt(result.get("triggerIndex"));

                Entity sourceEntity = null;
                String sourceIdStr = decisionData.get("sourceId");
                if (sourceIdStr != null)
                    sourceEntity = serverEntityIdSystem.findfromId(sourceIdStr);

                String triggerType = decisionData.get("triggerType");
                CardFilter triggerFilter = TriggerRequirements.createMandatoryTriggerRequirements(
                        decisionPlayer, triggerType,
                        cardFilteringSystem);
                if (triggerFilter.accepts(sourceEntity, new Memory(decisionData), usedCardEntity)) {
                    int usableTriggerIndex = TriggerRequirements.findUsableTriggerIndex(usedCardEntity, triggerType, false,
                            new Memory(decisionData), cardAbilitySystem, conditionResolverSystem);
                    if (usableTriggerIndex == triggerIndex) {
                        return true;
                    }
                }
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
            sourceEntity = serverEntityIdSystem.findfromId(sourceIdStr);

        CardFilter triggerFilter = TriggerRequirements.createMandatoryTriggerRequirements(
                username, decisionData.get("triggerType"),
                cardFilteringSystem);
        return !cardFilteringSystem.hasCard(sourceEntity, new Memory(decisionData), "inPlay", triggerFilter);
    }

    @Override
    public void processDecision(String decisionPlayer, Memory memory, ObjectMap<String, String> decisionData, ObjectMap<String, String> result) {
        String action = result.get("action");
        if (action.equals("pass")) {
            int playersPassed = Integer.parseInt(memory.getValue("playersPassed", "0"));
            memory.setValue("playersPassed", String.valueOf(playersPassed + 1));
        } else if (action.equals("use")) {
            // Execute the use action
            String cardId = result.get("cardId");
            Entity usedCardId = serverEntityIdSystem.findfromId(cardId);
            int triggerIndex = Integer.parseInt(result.get("triggerIndex"));

            Entity memoryEntity = stackSystem.getTopMostStackEntityWithComponent(EffectMemoryComponent.class);
            Memory newEffectMemory = new Memory(memoryEntity.getComponent(EffectMemoryComponent.class).getMemory());
            newEffectMemory.setValue("playersPassed", "0");
            newEffectMemory.appendValue("usedIds", String.valueOf(cardId));

            Entity playCardEffect = spawnSystem.spawnEntity("game/effect/trigger/playTriggerEffect.template");
            EffectMemoryComponent effectMemory = playCardEffect.getComponent(EffectMemoryComponent.class);
            Memory triggerMemory = new Memory(effectMemory.getMemory());
            triggerMemory.setValue("usedCardId", String.valueOf(usedCardId));
            triggerMemory.setValue("triggerIndex", String.valueOf(triggerIndex));
            stackSystem.stackEntity(playCardEffect);
        }
    }

    @Override
    protected void processSystem() {

    }
}
