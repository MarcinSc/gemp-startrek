package com.gempukku.startrek.server.hall;

import com.artemis.Component;
import com.gempukku.libgdx.network.OwnedByMultipleComponent;
import com.gempukku.libgdx.network.ReplicateToOwner;

import java.util.ArrayList;
import java.util.List;

@ReplicateToOwner
public class PlayedGameComponent extends Component implements OwnedByMultipleComponent {
    private List<String> owners = new ArrayList<>();
    private String gameId;

    @Override
    public List<String> getOwners() {
        return owners;
    }

    @Override
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
