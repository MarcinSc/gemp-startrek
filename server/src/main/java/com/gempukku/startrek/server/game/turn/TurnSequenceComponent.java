package com.gempukku.startrek.server.game.turn;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class TurnSequenceComponent extends Component {
    private Array<String> players = new Array<>();
    private String lastPlayerTurn;

    public Array<String> getPlayers() {
        return players;
    }

    public String getLastPlayerTurn() {
        return lastPlayerTurn;
    }

    public void setLastPlayerTurn(String lastPlayerTurn) {
        this.lastPlayerTurn = lastPlayerTurn;
    }
}
