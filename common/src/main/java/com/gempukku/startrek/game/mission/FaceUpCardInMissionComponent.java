package com.gempukku.startrek.game.mission;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class FaceUpCardInMissionComponent extends Component {
    private String missionOwner;
    private int missionIndex;

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
}
