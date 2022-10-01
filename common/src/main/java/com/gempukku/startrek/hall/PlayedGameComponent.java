package com.gempukku.startrek.hall;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.server.config.annotation.OwnedByMultipleComponent;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToOwner;

@ReplicateToOwner
public class PlayedGameComponent extends Component implements OwnedByMultipleComponent {
    private String gameId;
    private Array<String> owners;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public Array<String> getOwners() {
        return owners;
    }

    @Override
    public void setOwners(Array<String> owners) {
        this.owners = owners;
    }
}
