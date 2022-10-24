package com.gempukku.startrek.game.mission;

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class MissionComponent extends Component {
    private String owner;
    private int missionIndex;
    private boolean completed;
    private ObjectMap<String, Integer> hiddenCardsCount = new ObjectMap<>();

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getMissionIndex() {
        return missionIndex;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setMissionIndex(int missionIndex) {
        this.missionIndex = missionIndex;
    }

    public ObjectMap<String, Integer> getHiddenCardsCount() {
        return hiddenCardsCount;
    }
}
