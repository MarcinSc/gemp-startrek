package com.gempukku.startrek.game.mission;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class AttemptedMissionComponent extends Component {
    private String missionId;
    private Array<String> attemptingPersonnel = new Array<>();

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
