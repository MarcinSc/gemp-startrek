package com.gempukku.startrek.game.zone;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class FaceUpCardInMissionComponent extends Component {
    private boolean stopped;
    private int rangeUsed;

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public int getRangeUsed() {
        return rangeUsed;
    }

    public void setRangeUsed(int rangeUsed) {
        this.rangeUsed = rangeUsed;
    }
}
