package com.gempukku.startrek.game.zone;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.OwnedComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class FaceDownCardInMissionComponent extends Component implements OwnedComponent {
    private String owner;
    private String missionOwner;
    private int missionIndex;
    private boolean stopped;
    private String onShipId;

    @Override
    public boolean isOwnedBy(String username) {
        return owner.equals(username);
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

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

    public String getOnShipId() {
        return onShipId;
    }

    public void setOnShipId(String onShipId) {
        this.onShipId = onShipId;
    }
}
