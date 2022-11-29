package com.gempukku.startrek.expression;

import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.common.StringUtils;

public class Expression {
    private String type;
    private Array<String> parameters;

    public Expression(String type) {
        this.type = type;
    }

    public Expression(String type, Array<String> parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public Array<String> getParameters() {
        return parameters;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (parameters != null && parameters.size > 0) {
            sb.append("(");
            sb.append(StringUtils.merge(parameters));
            sb.append(")");
        }
        return sb.toString();
    }
}
