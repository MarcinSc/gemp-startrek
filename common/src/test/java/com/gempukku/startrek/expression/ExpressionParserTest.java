package com.gempukku.startrek.expression;

import com.badlogic.gdx.utils.Array;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExpressionParserTest {
    private ExpressionParser expressionParser = new ExpressionParser();

    @Test
    public void simpleExpression() {
        Array<Expression> expressions = expressionParser.parseExpression("test");
        assertEquals(1, expressions.size);
        Expression expression = expressions.get(0);
        assertEquals("test", expression.getType());
        assertNull(expression.getParameters());
    }

    @Test
    public void expressionWithParameter() {
        Array<Expression> expressions = expressionParser.parseExpression("test(param)");
        assertEquals(1, expressions.size);
        Expression expression = expressions.get(0);
        Array<String> parameters = expression.getParameters();
        assertEquals("test", expression.getType());
        assertEquals(1, parameters.size);
        assertEquals("param", parameters.get(0));
    }

    @Test
    public void multipleParameters() {
        Array<Expression> expressions = expressionParser.parseExpression("test(param1, param2)");
        assertEquals(1, expressions.size);
        Expression expression = expressions.get(0);
        Array<String> parameters = expression.getParameters();
        assertEquals("test", expression.getType());
        assertEquals(2, parameters.size);
        assertEquals("param1", parameters.get(0));
        assertEquals("param2", parameters.get(1));
    }

    @Test
    public void multipleExpressions() {
        Array<Expression> expressions = expressionParser.parseExpression("test1, test2");
        assertEquals(2, expressions.size);
        Expression expression1 = expressions.get(0);
        Expression expression2 = expressions.get(1);
        assertEquals("test1", expression1.getType());
        assertNull(expression1.getParameters());
        assertEquals("test2", expression2.getType());
        assertNull(expression2.getParameters());
    }

    @Test
    public void nested() {
        Array<Expression> expressions = expressionParser.parseExpression("test(nested1(param), nested2)");
        assertEquals(1, expressions.size);
        Expression expression = expressions.get(0);
        Array<String> parameters = expression.getParameters();
        assertEquals("test", expression.getType());
        assertEquals(2, parameters.size);
        assertEquals("nested1(param)", parameters.get(0));
        assertEquals("nested2", parameters.get(1));
    }
}