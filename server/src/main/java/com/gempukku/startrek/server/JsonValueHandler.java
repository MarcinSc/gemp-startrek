package com.gempukku.startrek.server;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

public class JsonValueHandler {
    private static final JsonReader jsonReader = new JsonReader();

    public static JsonValue clone(JsonValue jsonValue) {
        if (jsonValue == null)
            return null;
        return jsonReader.parse(jsonValue.toJson(JsonWriter.OutputType.json));
    }
}
