package com.gempukku.startrek.server.game.condition;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.expression.Expression;
import com.gempukku.startrek.expression.ExpressionSystem;

public class ConditionResolverSystem extends BaseSystem {
    private ExpressionSystem expressionSystem;

    private ObjectMap<String, ConditionHandler> conditionHandlers = new ObjectMap<>();

    public ConditionResolverSystem() {
        registerConditionHandler("false",
                new ConditionHandler() {
                    @Override
                    public boolean resolveCondition(String type, Entity sourceEntity, ObjectMap<String, String> memory, Array<String> parameters) {
                        return false;
                    }
                });
        registerConditionHandler("true",
                new ConditionHandler() {
                    @Override
                    public boolean resolveCondition(String type, Entity sourceEntity, ObjectMap<String, String> memory, Array<String> parameters) {
                        return true;
                    }
                });
    }

    public void registerConditionHandler(String effectType, ConditionHandler conditionHandler) {
        conditionHandlers.put(effectType, conditionHandler);
    }

    public boolean resolveBoolean(Entity sourceEntity, ObjectMap<String, String> memory, String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        for (Expression expression : expressions) {
            String type = expression.getType();
            ConditionHandler conditionHandler = conditionHandlers.get(type);
            if (conditionHandler == null)
                throw new RuntimeException("Unable to find condition handler: " + type);

            boolean result = conditionHandler.resolveCondition(type, sourceEntity, memory, expression.getParameters());
            if (!result)
                return false;
        }
        return true;
    }

    @Override
    protected void processSystem() {

    }
}
