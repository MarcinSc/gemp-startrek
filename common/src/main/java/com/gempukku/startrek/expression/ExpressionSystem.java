package com.gempukku.startrek.expression;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;

public class ExpressionSystem extends BaseSystem {
    private ExpressionParser expressionParser = new ExpressionParser();

    public Array<Expression> parseExpression(String expression) {
        return expressionParser.parseExpression(expression);
    }

    @Override
    protected void processSystem() {

    }
}
