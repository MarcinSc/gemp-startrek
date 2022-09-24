package com.gempukku.startrek.game.turn;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class TurnComponent extends Component {
    private String player;
    private TurnSegment turnSegment;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public TurnSegment getTurnSegment() {
        return turnSegment;
    }

    public void setTurnSegment(TurnSegment turnSegment) {
        this.turnSegment = turnSegment;
    }
}
