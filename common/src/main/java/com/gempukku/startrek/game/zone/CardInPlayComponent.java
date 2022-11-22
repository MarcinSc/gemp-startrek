package com.gempukku.startrek.game.zone;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateWithOthers;

@ReplicateWithOthers
public class CardInPlayComponent extends Component {
    private boolean stopped;
    private String attachedToId;
    private int rangeUsed;
    private int attachedFaceDownCount;

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

    public int getRangeUsed() {
        return rangeUsed;
    }

    public void setRangeUsed(int rangeUsed) {
        this.rangeUsed = rangeUsed;
    }

    public int getAttachedFaceDownCount() {
        return attachedFaceDownCount;
    }

    public void setAttachedFaceDownCount(int attachedFaceDownCount) {
        this.attachedFaceDownCount = attachedFaceDownCount;
    }
}
