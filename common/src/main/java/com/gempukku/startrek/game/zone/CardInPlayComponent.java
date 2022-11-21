package com.gempukku.startrek.game.zone;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateWithOthers;

@ReplicateWithOthers
public class CardInPlayComponent extends Component {
    private boolean stopped;
    private String attachedToId;

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public String getAttachedToId() {
        return attachedToId;
    }

    public void setAttachedToId(String attachedToId) {
        this.attachedToId = attachedToId;
    }
}
