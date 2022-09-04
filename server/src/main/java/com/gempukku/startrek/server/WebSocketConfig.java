package com.gempukku.startrek.server;

import com.gempukku.startrek.server.hall.StarTrekHallContext;
import com.gempukku.startrek.server.hall.StarTrekHallWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private StarTrekHallContext starTrekHallContext;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(version1Handler(), "/game");
        registry.addHandler(mainHallHandler(), "/hall");
    }

    private WebSocketHandler mainHallHandler() {
        return new StarTrekHallWebSocketHandler(starTrekHallContext);
    }
//
//    @Bean
//    public WebSocketHandler version1Handler() {
//        return new OverpowerGameWebSocketHandler(overpowerHallContext);
//    }
}