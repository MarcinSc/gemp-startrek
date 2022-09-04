package com.gempukku.startrek.server.websocket;

public interface PlayerListener {
    void playerEntered(String username);
    void playerLeft(String username);
}
