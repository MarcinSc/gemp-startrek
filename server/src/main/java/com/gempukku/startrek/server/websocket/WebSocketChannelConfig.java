package com.gempukku.startrek.server.websocket;

import org.springframework.web.socket.WebSocketHandler;

public interface WebSocketChannelConfig extends WebSocketHandler {
    String getPath();
}
