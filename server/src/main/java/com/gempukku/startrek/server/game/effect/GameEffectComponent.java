package com.gempukku.startrek.server.game.effect;

import com.artemis.Component;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.startrek.server.JsonValueHandler;

public class GameEffectComponent extends Component {
    private String sourceEntityId = null;
    private String type;
    private JsonValue data;

    public String getSourceEntityId() {
        return sourceEntityId;
    }

    public void setSourceEntityId(String sourceEntityId) {
        this.sourceEntityId = sourceEntityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setData(JsonValue data) {
        this.data = data;
    }

    public JsonValue getClonedData() {
        return JsonValueHandler.clone(data);
    }

    public JsonValue getClonedDataObject(String name) {
        if (!data.has(name))
            return null;
        JsonValue result = data.get(name);
        return JsonValueHandler.clone(result);
    }

    public String getDataString(String name) {
        return data.getString(name);
    }

    public String getDataString(String name, String defaultValue) {
        return data.getString(name, defaultValue);
    }

    public boolean getDataBoolean(String name, boolean defaultValue) {
        if (data.has(name))
            return data.getBoolean(name);
        return defaultValue;
    }
}
