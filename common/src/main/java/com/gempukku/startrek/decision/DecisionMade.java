package com.gempukku.startrek.decision;

import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.network.EventFromClient;
import com.gempukku.libgdx.network.SendToServer;

@SendToServer
public class DecisionMade extends EventFromClient {
    private ObjectMap<String, String> parameters;

    public DecisionMade() {
        this(new ObjectMap<>());
    }

    public DecisionMade(ObjectMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public ObjectMap<String, String> getParameters() {
        return parameters;
    }
}
