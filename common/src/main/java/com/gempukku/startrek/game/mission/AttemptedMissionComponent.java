package com.gempukku.startrek.game.mission;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.server.config.annotation.OwnedComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class AttemptedMissionComponent extends Component implements OwnedComponent {
    private String owner;
    private String missionId;
    private Array<String> attemptingPersonnel = new Array<>();

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean isOwnedBy(String username) {
        return username.equals(owner);
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public Array<String> getAttemptingPersonnel() {
        return attemptingPersonnel;
    }
}
