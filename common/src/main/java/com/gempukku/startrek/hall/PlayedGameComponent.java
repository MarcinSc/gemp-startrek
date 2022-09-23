package com.gempukku.startrek.hall;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.OwnedByMultipleComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

import java.util.List;

@ReplicateToOwner
public class PlayedGameComponent extends Component implements OwnedByMultipleComponent {
    private String gameId;
    private List<String> owners;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public List<String> getOwners() {
        return owners;
    }

    @Override
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }
}
