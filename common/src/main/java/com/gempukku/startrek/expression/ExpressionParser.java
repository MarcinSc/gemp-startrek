package com.gempukku.startrek.expression;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class ExpressionParser {
    private ObjectMap<String, Array<Expression>> expressionCache = new ObjectMap<>();

    public Array<Expression> parseExpression(String text) {
        Array<Expression> expressions = expressionCache.get(text);
        if (expressions == null) {
            expressions = parseExpressionInternal(text);
            expressionCache.put(text, expressions);
        }
        return expressions;
    }

    private Array<Expression> parseExpressionInternal(String value) {
        Array<Expression> result = new Array<>();
        final char[] chars = value.toCharArray();

        int depth = 0;
        StringBuilder sb = new StringBuilder();
        for (char ch : chars) {
            if (depth > 0) {
                if (ch == ')')
                    depth--;
                if (ch == '(')
                    depth++;
                sb.append(ch);
            } else {
                if (ch == ',') {
                    result.add(createExpression(sb.toString()));
                    sb = new StringBuilder();
                } else {
                    if (ch == ')')
                        throw new RuntimeException("Invalid expression definition: " + value);
                    if (ch == '(')
                        depth++;
                    sb.append(ch);
                }
            }
        }

        if (depth != 0)
            throw new RuntimeException("Not matching number of opening and closing brackets: " + value);

        result.add(createExpression(sb.toString()));

        return result;
    }

    private Expression createExpression(String text) {
        int openingParenthesisIndex = text.indexOf("(");
        if (openingParenthesisIndex > -1) {
            String type = text.substring(0, openingParenthesisIndex).trim();
            Array<String> parameters = createParameters(text.substring(openingParenthesisIndex + 1, text.length() - 1));
            return new Expression(type, parameters);
        } else {
            return new Expression(text.trim());
        }
    }

    private Array<String> createParameters(String text) {
        Array<String> parameters = new Array<>();
        final char[] chars = text.toCharArray();

        int depth = 0;
        StringBuilder sb = new StringBuilder();
        for (char ch : chars) {
            if (depth > 0) {
                if (ch == ')')
                    depth--;
                if (ch == '(')
                    depth++;
                sb.append(ch);
            } else {
                if (ch == ',' && depth == 0) {
                    parameters.add(sb.toString().trim());
                    sb = new StringBuilder();
                } else {
                    if (ch == ')')
                        throw new RuntimeException("Invalid expression definition: " + text);
                    if (ch == '(')
                        depth++;
                    sb.append(ch);
                }
            }
        }

        parameters.add(sb.toString().trim());

        return parameters;
    }
}
