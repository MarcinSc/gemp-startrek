package com.gempukku.startrek.game.turn;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class TurnSequenceComponent extends Component {
    private Array<String> players = new Array<>();
    private String currentPlayer;

    public Array<String> getPlayers() {
        return players;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
