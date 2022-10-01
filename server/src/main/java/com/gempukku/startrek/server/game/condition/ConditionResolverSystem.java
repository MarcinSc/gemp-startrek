package com.gempukku.startrek.server.game.condition;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.expression.Expression;
import com.gempukku.startrek.expression.ExpressionSystem;

public class ConditionResolverSystem extends BaseSystem {
    private ExpressionSystem expressionSystem;

    private ObjectMap<String, ConditionHandler> conditionHandlers = new ObjectMap<>();

    public void registerConditionHandler(String effectType, ConditionHandler conditionHandler) {
        conditionHandlers.put(effectType, conditionHandler);
    }

    public boolean resolveBoolean(ObjectMap<String, String> memory, JsonValue jsonValue) {
        if (jsonValue.type() == JsonValue.ValueType.booleanValue)
            return jsonValue.asBoolean();
        if (jsonValue.type() == JsonValue.ValueType.stringValue) {
            String text = jsonValue.asString();
            Array<Expression> expressions = expressionSystem.parseExpression(text);
            for (Expression expression : expressions) {
                String type = expression.getType();
                ConditionHandler conditionHandler = conditionHandlers.get(type);
                if (conditionHandler == null)
                    throw new RuntimeException("Unable to find condition handler: " + type);

                boolean result = conditionHandler.resolveCondition(memory, type, expression.getParameters());
                if (!result)
                    return false;
            }
        }

        throw new RuntimeException("Unable to resolve boolean: " + jsonValue.toJson(JsonWriter.OutputType.json));
    }

    @Override
    protected void processSystem() {

    }
}
