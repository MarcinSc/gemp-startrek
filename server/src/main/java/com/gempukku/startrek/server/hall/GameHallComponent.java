package com.gempukku.startrek.server.hall;

import com.artemis.Component;
import com.gempukku.libgdx.network.ReplicateToClients;

@ReplicateToClients
public class GameHallComponent extends Component {
    private int userCount;
    private int gameCount;

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }
}
