package com.gempukku.startrek.expression;

import com.badlogic.gdx.utils.Array;

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
}
