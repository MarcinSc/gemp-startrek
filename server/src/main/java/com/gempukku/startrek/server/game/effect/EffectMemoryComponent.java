package com.gempukku.startrek.server.game.effect;

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectMap;

public class EffectMemoryComponent extends Component {
    private ObjectMap<String, String> memory = new ObjectMap<>();
    private String memoryType;

    public ObjectMap<String, String> getMemory() {
        return memory;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public String getMemoryType() {
        return memoryType;
    }
}
