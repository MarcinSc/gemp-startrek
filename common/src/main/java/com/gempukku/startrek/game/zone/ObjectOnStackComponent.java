package com.gempukku.startrek.game.zone;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class ObjectOnStackComponent extends Component {
    private String type;
    private int stackIndex;
    private int effectStep = -1;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStackIndex() {
        return stackIndex;
    }

    public void setStackIndex(int stackIndex) {
        this.stackIndex = stackIndex;
    }

    public int getEffectStep() {
        return effectStep;
    }

    public void setEffectStep(int effectStep) {
        this.effectStep = effectStep;
    }
}
