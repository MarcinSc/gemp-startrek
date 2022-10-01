package com.gempukku.startrek.server.game.decision;

import com.artemis.Component;

public class PlayerDecisionComponent extends Component {
    private String player;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
