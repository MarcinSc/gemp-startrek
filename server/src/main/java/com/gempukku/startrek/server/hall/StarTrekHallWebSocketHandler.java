package com.gempukku.startrek.server.hall;

import com.artemis.World;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.JsonDataSerializer;
import com.gempukku.libgdx.network.server.RemoteEntityManagerHandler;
import com.gempukku.startrek.server.hall.event.PlayerConnected;
import com.gempukku.startrek.server.hall.event.PlayerDisconnected;
import com.gempukku.startrek.server.hall.event.UpdateGames;
import com.gempukku.startrek.server.websocket.OneConnectionPerUserIntoContext;
import com.gempukku.startrek.server.websocket.PlayerListener;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class StarTrekHallWebSocketHandler extends TextWebSocketHandler {
    private OneConnectionPerUserIntoContext oneConnectionPerUserIntoContext;

    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1,
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
                            "Hall-Executor",
                            0);
                    if (t.isDaemon())
                        t.setDaemon(false);
                    if (t.getPriority() != Thread.NORM_PRIORITY)
                        t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                }
            });

    public StarTrekHallWebSocketHandler(StarTrekHallContext gameHallContext) {
        World hallEntityWorld = gameHallContext.getHallEntityWorld();
        EventSystem eventSystem = hallEntityWorld.getSystem(EventSystem.class);
        HallEntityProviderSystem hallEntityProvider = hallEntityWorld.getSystem(HallEntityProviderSystem.class);
        oneConnectionPerUserIntoContext = new OneConnectionPerUserIntoContext(
                executor, hallEntityWorld.getSystem(RemoteEntityManagerHandler.class),
                new JsonDataSerializer(),
                new PlayerListener() {
                    @Override
                    public void playerEntered(String username) {
                        eventSystem.fireEvent(new PlayerConnected(username), hallEntityProvider.getGameHallEntity());
                    }

                    @Override
                    public void playerLeft(String username) {
                        eventSystem.fireEvent(new PlayerDisconnected(username), hallEntityProvider.getGameHallEntity());
                    }
                });
        executor.scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run() {
                        eventSystem.fireEvent(new UpdateGames(), hallEntityProvider.getGameHallEntity());
                        hallEntityWorld.process();
                    }
                }, 100, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        oneConnectionPerUserIntoContext.connectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        oneConnectionPerUserIntoContext.messageReceived(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        oneConnectionPerUserIntoContext.connectionClosed(session);
    }
}
