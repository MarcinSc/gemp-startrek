package com.gempukku.startrek.server.hall.event;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;

public class PlayerDisconnected implements EntityEvent {
    private String username;

    public PlayerDisconnected(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
