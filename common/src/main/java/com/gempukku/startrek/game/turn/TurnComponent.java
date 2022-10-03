package com.gempukku.startrek.game.turn;

import com.artemis.Component;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClients;

@ReplicateToClients
public class TurnComponent extends Component {
    private TurnSegment turnSegment;

    public TurnSegment getTurnSegment() {
        return turnSegment;
    }

    public void setTurnSegment(TurnSegment turnSegment) {
        this.turnSegment = turnSegment;
    }
}
