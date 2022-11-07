package com.gempukku.startrek.server.game.effect;

import com.artemis.Component;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.startrek.server.JsonValueHandler;

public class GameEffectComponent extends Component {
    private String type;
    private JsonValue data;
    private ObjectMap<String, String> memory = new ObjectMap<>();

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
        return JsonValueHandler.clone(data.get(name));
    }

    public String getDataString(String name) {
        return data.getString(name);
    }

    public ObjectMap<String, String> getMemory() {
        return memory;
    }
}
