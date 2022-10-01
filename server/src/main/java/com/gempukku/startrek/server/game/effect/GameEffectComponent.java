package com.gempukku.startrek.server.game.effect;

import com.artemis.Component;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

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

    public JsonValue getData() {
        return data;
    }

    public void setData(JsonValue data) {
        this.data = data;
    }

    public ObjectMap<String, String> getMemory() {
        return memory;
    }
}
