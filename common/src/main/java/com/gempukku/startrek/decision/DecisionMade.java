package com.gempukku.startrek.decision;

import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.network.EventFromClient;

public class DecisionMade extends EventFromClient {
    private ObjectMap<String, String> parameters = new ObjectMap<>();

    public ObjectMap<String, String> getParameters() {
        return parameters;
    }
}
