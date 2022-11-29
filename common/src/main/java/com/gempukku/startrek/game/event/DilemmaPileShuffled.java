package com.gempukku.startrek.game.event;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.network.server.config.annotation.SendToClients;

@SendToClients
public class DilemmaPileShuffled implements EntityEvent {
    private String player;

    public DilemmaPileShuffled() {
    }

    public DilemmaPileShuffled(String player) {
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
