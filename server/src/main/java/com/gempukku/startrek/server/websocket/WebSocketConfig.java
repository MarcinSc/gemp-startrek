package com.gempukku.startrek.server.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private List<WebSocketChannelConfig> channelConfigs;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        for (WebSocketChannelConfig channelConfig : channelConfigs) {
            registry.addHandler(channelConfig, channelConfig.getPath());
        }
    }
}