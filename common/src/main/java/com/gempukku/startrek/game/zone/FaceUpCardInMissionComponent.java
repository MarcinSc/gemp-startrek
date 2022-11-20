package com.gempukku.startrek.game.zone;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class FaceUpCardInMissionComponent extends Component {
    private String missionOwner;
    private int missionIndex;
    private boolean stopped;
    private int rangeUsed;

    public String getMissionOwner() {
        return missionOwner;
    }

    public void setMissionOwner(String missionOwner) {
        this.missionOwner = missionOwner;
    }

    public int getMissionIndex() {
        return missionIndex;
    }

    public void setMissionIndex(int missionIndex) {
        this.missionIndex = missionIndex;
    }

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
