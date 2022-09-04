package com.gempukku.startrek.server.hall.event;

import com.gempukku.libgdx.lib.artemis.event.EntityEvent;

public class PlayerConnected implements EntityEvent {
    private String username;

    public PlayerConnected(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
