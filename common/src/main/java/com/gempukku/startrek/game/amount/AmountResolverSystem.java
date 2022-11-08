package com.gempukku.startrek.game.amount;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.expression.Expression;
import com.gempukku.startrek.expression.ExpressionSystem;
import com.gempukku.startrek.game.Memory;

public class AmountResolverSystem extends BaseSystem {
    private ExpressionSystem expressionSystem;

    private ObjectMap<String, AmountHandler> amountHandlers = new ObjectMap<>();

    public void registerAmountHandler(String type, AmountHandler amountHandler) {
        amountHandlers.put(type, amountHandler);
    }

    public int resolveAmount(Entity sourceEntity, Memory memory, String value) {
        Array<Expression> expressions = expressionSystem.parseExpression(value);
        if (expressions.size != 1)
            throw new GdxRuntimeException("Unable to resolve amount handler: " + value);

        Expression expression = expressions.get(0);
        String type = expression.getType();
        AmountHandler amountHandler = amountHandlers.get(type);
        if (amountHandler == null) {
            // Check if it's just a number
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exp) {

            }
            throw new GdxRuntimeException("Unable to find amount handler: " + type);
        }

        return amountHandler.resolveAmount(type, sourceEntity, memory, expression.getParameters());
    }

    @Override
    protected void processSystem() {

    }
}
