package com.gempukku.startrek.game.condition;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.expression.Expression;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;

public class ConditionResolverSystem extends BaseSystem {
    private ExpressionSystem expressionSystem;

    private final ObjectMap<String, ConditionHandler> conditionHandlers = new ObjectMap<>();

    public ConditionResolverSystem() {
        registerConditionHandler("false",
                new ConditionHandler() {
                    @Override
                    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
                        return false;
                    }

                    @Override
                    public void validate(Array<String> parameters) {
                        ValidateUtil.exactly(parameters, 0);
                    }
                });
        registerConditionHandler("true",
                new ConditionHandler() {
                    @Override
                    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
                        return true;
                    }

                    @Override
                    public void validate(Array<String> parameters) {

                    }
                });
        registerConditionHandler("and",
                new ConditionHandler() {
                    @Override
                    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
                        for (String parameter : parameters) {
                            if (!resolveBoolean(sourceEntity, memory, parameter))
                                return false;
                        }

                        return true;
                    }

                    @Override
                    public void validate(Array<String> parameters) {
                        ValidateUtil.atLeast(parameters, 2);
                        for (String parameter : parameters) {
                            ConditionResolverSystem.this.validate(parameter);
                        }

                    }
                });
        registerConditionHandler("or",
                new ConditionHandler() {
                    @Override
                    public boolean resolveCondition(String type, Entity sourceEntity, Memory memory, Array<String> parameters) {
                        for (String parameter : parameters) {
                            if (resolveBoolean(sourceEntity, memory, parameter))
                                return true;
                        }

                        return false;
                    }

                    @Override
                    public void validate(Array<String> parameters) {
                        ValidateUtil.atLeast(parameters, 2);
                        for (String parameter : parameters) {
                            ConditionResolverSystem.this.validate(parameter);
                        }

                    }
                });
    }

    public void registerConditionHandler(String effectType, ConditionHandler conditionHandler) {
        conditionHandlers.put(effectType, conditionHandler);
    }

    public boolean resolveBoolean(Entity sourceEntity, Memory memory, String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        for (Expression expression : expressions) {
            String type = expression.getType();
            ConditionHandler conditionHandler = conditionHandlers.get(type);
            if (conditionHandler == null)
                throw new GdxRuntimeException("Unable to find condition handler: " + type);

            boolean result = conditionHandler.resolveCondition(type, sourceEntity, memory, expression.getParameters());
            if (!result)
                return false;
        }
        return true;
    }

    public void validate(String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        for (Expression expression : expressions) {
            String type = expression.getType();
            ConditionHandler conditionHandler = conditionHandlers.get(type);
            if (conditionHandler == null)
                throw new GdxRuntimeException("Unable to find condition handler: " + type);

            conditionHandler.validate(expression.getParameters());
        }
    }

    @Override
    protected void processSystem() {

    }
}
