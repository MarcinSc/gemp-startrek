package com.gempukku.startrek.game.mission;

import com.artemis.Component;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class MissionComponent extends Component {
    private boolean completed;
    private ObjectMap<String, Integer> playerFaceDownCardsCount = new ObjectMap<>();

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public ObjectMap<String, Integer> getPlayerFaceDownCardsCount() {
        return playerFaceDownCardsCount;
    }
}
