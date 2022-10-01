package com.gempukku.startrek.server.game;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.utils.Disposable;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.event.RuntimeEntityEventDispatcher;
import com.gempukku.libgdx.network.JsonDataSerializer;
import com.gempukku.libgdx.network.server.RemoteEntityManagerHandler;
import com.gempukku.libgdx.network.server.config.annotation.SerializeToClientsConfig;
import com.gempukku.startrek.server.common.NetworkEntityConfigurationSystem;
import com.gempukku.startrek.server.common.ServerSpawnSystem;
import com.gempukku.startrek.server.websocket.OneConnectionPerUserIntoContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ExecutorService;

public class StarTrekGameHolder implements Disposable {
    private World gameWorld;
    private OneConnectionPerUserIntoContext oneConnectionPerUserIntoContext;

    public StarTrekGameHolder(ExecutorService executorService) {
        gameWorld = createGameWorld();

        oneConnectionPerUserIntoContext = new OneConnectionPerUserIntoContext(
                executorService, gameWorld.getSystem(RemoteEntityManagerHandler.class),
                new JsonDataSerializer());

        oneConnectionPerUserIntoContext.addNetworkEntitySerializationConfig(
                new SerializeToClientsConfig());
    }

    public void connectUser(WebSocketSession session) {
        oneConnectionPerUserIntoContext.connectionEstablished(session);
    }

    public void messageReceived(WebSocketSession session, TextMessage message) {
        oneConnectionPerUserIntoContext.messageReceived(session, message);
    }

    public void sessionClosed(WebSocketSession session) {
        oneConnectionPerUserIntoContext.connectionClosed(session);
    }

    private static World createGameWorld() {
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        worldConfigurationBuilder.with(
                // Base systems
                new ServerSpawnSystem(),
                new EventSystem(new RuntimeEntityEventDispatcher()),

                // Network systems
                new RemoteEntityManagerHandler(),
                new NetworkEntityConfigurationSystem());

        return new World(worldConfigurationBuilder.build());
    }

    @Override
    public void dispose() {
        oneConnectionPerUserIntoContext.closeAllConnections();
        gameWorld.dispose();
    }
}
