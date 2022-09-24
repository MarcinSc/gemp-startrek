package com.gempukku.startrek.game;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class GameComponent extends Component {
    private Array<String> players = new Array<>();

    public Array<String> getPlayers() {
        return players;
    }
}
