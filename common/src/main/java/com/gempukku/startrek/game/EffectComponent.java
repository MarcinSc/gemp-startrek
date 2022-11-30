package com.gempukku.startrek.game;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateWithOthers;

@ReplicateWithOthers
public class EffectComponent extends Component {
    // Id of the card that created that effect
    private String sourceId;
    // Card identifier of the card that created that effect
    private String sourceCardId;
    // Owner of the effect
    private String owner;
    // Type of special action
    private String specialAction;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceCardId() {
        return sourceCardId;
    }

    public void setSourceCardId(String sourceCardId) {
        this.sourceCardId = sourceCardId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSpecialAction() {
        return specialAction;
    }

    public void setSpecialAction(String specialAction) {
        this.specialAction = specialAction;
    }
}
