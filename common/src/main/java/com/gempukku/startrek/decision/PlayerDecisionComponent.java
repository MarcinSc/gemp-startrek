package com.gempukku.startrek.decision;

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.network.server.config.annotation.OwnedComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class PlayerDecisionComponent extends Component implements OwnedComponent {
    private String owner;
    private String decisionType;
    private ObjectMap<String, String> data = new ObjectMap<>();

    @Override
    public boolean isOwnedBy(String username) {
        return owner.equals(username);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    public ObjectMap<String, String> getData() {
        return data;
    }
}
