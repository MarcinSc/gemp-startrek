package com.gempukku.startrek.game.mission;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.OwnedComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class HiddenCardInMissionComponent extends Component implements OwnedComponent {
    private String missionOwner;
    private int missionIndex;
    private String cardOwner;

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

    @Override
    public String getOwner() {
        return cardOwner;
    }

    @Override
    public void setOwner(String owner) {
        this.cardOwner = cardOwner;
    }
}
