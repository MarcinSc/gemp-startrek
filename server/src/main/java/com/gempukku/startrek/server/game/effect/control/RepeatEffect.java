package com.gempukku.startrek.server.game.effect.control;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.turn.TurnSequenceComponent;
import com.gempukku.startrek.server.game.effect.EffectSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.GameEffectSystem;

public class RepeatEffect extends EffectSystem {
    private GameEffectSystem gameEffectSystem;
    private AmountResolverSystem amountResolverSystem;
    private ConditionResolverSystem conditionResolverSystem;
    private ComponentMapper<GameEffectComponent> gameEffectComponentMapper;

    public RepeatEffect() {
        super("repeatTimes", "repeatUntil", "repeatUntilInTurnOrder");
    }

    @Override
    public void processEffect(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        String type = gameEffect.getType();
        if (type.equals("repeatTimes")) {
            repeat(sourceEntity, memory, effectEntity, gameEffect);
        } else if (type.equals("repeatUntil")) {
            repeatUntil(sourceEntity, memory, effectEntity, gameEffect);
        } else if (type.equals("repeatUntilInTurnOrder")) {
            repeatUntilInTurnOrder(sourceEntity, memory, effectEntity, gameEffect);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        String type = effect.getString("type");
        if (type.equals("repeatTimes")) {
            ValidateUtil.effectExpectedFields(effect,
                    new String[]{"times", "action"},
                    new String[]{});
            amountResolverSystem.validateAmount(effect.getString("times"));
            validateOneEffect(effect.get("action"));
        } else if (type.equals("repeatUntil")) {
            ValidateUtil.effectExpectedFields(effect,
                    new String[]{"condition", "action"},
                    new String[]{});
            conditionResolverSystem.validateCondition(effect.getString("condition"));
            validateOneEffect(effect.get("action"));
        } else if (type.equals("repeatUntilInTurnOrder")) {
            ValidateUtil.effectExpectedFields(effect,
                    new String[]{"condition", "playerMemory", "action"},
                    new String[]{});
            conditionResolverSystem.validateCondition(effect.getString("condition"));
            validateOneEffect(effect.get("action"));
        }
    }

    private void repeat(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        int times = amountResolverSystem.resolveAmount(sourceEntity, memory,
                gameEffect.getDataString("times"));

        String memoryName = getMemoryName(effectEntity, "repeatedTimes");

        int executedTimes = 0;
        String executed = memory.getValue(memoryName);
        if (executed != null) {
            executedTimes = Integer.parseInt(executed);
        }

        if (executedTimes < times) {
            JsonValue action = gameEffect.getClonedDataObject("action");
            Entity actionToStack = createActionFromJson(action, sourceEntity);
            executedTimes++;
            memory.setValue(memoryName, String.valueOf(executedTimes));

            stackEffect(actionToStack);
        } else {
            memory.removeValue(memoryName);
            removeTopEffectFromStack();
        }
    }

    private void repeatUntil(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        String condition = gameEffect.getDataString("condition");
        boolean result = conditionResolverSystem.resolveBoolean(sourceEntity, memory, condition);
        if (!result) {
            JsonValue action = gameEffect.getClonedDataObject("action");
            Entity actionToStack = createActionFromJson(action, sourceEntity);
            stackEffect(actionToStack);
        } else {
            removeTopEffectFromStack();
        }
    }

    private void repeatUntilInTurnOrder(Entity sourceEntity, Memory memory, Entity effectEntity, GameEffectComponent gameEffect) {
        boolean condition = conditionResolverSystem.resolveBoolean(sourceEntity, memory,
                gameEffect.getDataString("condition"));
        String memoryName = getMemoryName(effectEntity, "lastPlayerIndex");
        String playerMemoryName = gameEffect.getDataString("playerMemory");

        if (!condition) {
            TurnSequenceComponent turnSequence = LazyEntityUtil.findEntityWithComponent(world, TurnSequenceComponent.class).
                    getComponent(TurnSequenceComponent.class);
            Array<String> players = turnSequence.getPlayers();


            String playerIndex = memory.getValue(memoryName);
            int nextPlayerIndex = 0;
            if (playerIndex != null) {
                nextPlayerIndex = Integer.parseInt(playerIndex) + 1;
            }

            if (nextPlayerIndex == players.size)
                nextPlayerIndex = 0;

            String player = players.get(nextPlayerIndex);
            memory.setValue(playerMemoryName, player);
            JsonValue action = gameEffect.getClonedDataObject("action");
            memory.setValue(memoryName, String.valueOf(nextPlayerIndex));

            Entity actionToStack = createActionFromJson(action, sourceEntity);

            stackEffect(actionToStack);
        } else {
            memory.removeValue(playerMemoryName);
            memory.removeValue(memoryName);
            removeTopEffectFromStack();
        }
    }
}
