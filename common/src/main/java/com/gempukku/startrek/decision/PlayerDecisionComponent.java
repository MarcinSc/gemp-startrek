package com.gempukku.startrek.decision;

import com.artemis.Component;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.network.server.config.annotation.OwnedComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class PlayerDecisionComponent extends Component implements OwnedComponent {
    private String owner;
    private String decisionType;
    private JsonValue data;

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    public JsonValue getData() {
        return data;
    }

    public void setData(JsonValue data) {
        this.data = data;
    }
}
