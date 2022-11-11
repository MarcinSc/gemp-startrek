package com.gempukku.startrek.server.websocket;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.network.DataSerializer;
import com.gempukku.libgdx.network.EventFromClient;
import com.gempukku.libgdx.network.NetworkMessage;
import com.gempukku.libgdx.network.SendToServer;
import com.gempukku.libgdx.network.json.JsonValueNetworkMessageMarshaller;
import com.gempukku.libgdx.network.server.ClientConnection;
import com.gempukku.libgdx.network.server.RemoteEntityManagerHandler;
import com.gempukku.libgdx.network.server.RemoteHandler;
import com.gempukku.libgdx.network.server.SerializingClientConnection;
import com.gempukku.libgdx.network.server.config.NetworkEntitySerializationConfig;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class OneConnectionPerUserIntoContext implements NetworkEntitySerializationConfig {
    private Map<WebSocketSession, SerializingClientConnection<JsonValue>> clientConnections = new HashMap<>();
    private Map<String, WebSocketSession> userSessions = new HashMap<>();
    private JsonValueNetworkMessageMarshaller networkMessageMarshaller = new JsonValueNetworkMessageMarshaller();

    private ExecutorService executorService;
    private RemoteHandler remoteHandler;
    private DataSerializer<JsonValue> dataSerializer;
    private PlayerListener playerListener;

    private Array<NetworkEntitySerializationConfig> networkEntitySerializationConfigArray = new Array<>();

    public OneConnectionPerUserIntoContext(ExecutorService executorService, RemoteEntityManagerHandler remoteHandler, DataSerializer<JsonValue> jsonEntitySerializer) {
        this(executorService, remoteHandler, jsonEntitySerializer, null);
    }

    public OneConnectionPerUserIntoContext(ExecutorService executorService, RemoteEntityManagerHandler remoteHandler, DataSerializer<JsonValue> jsonEntitySerializer, PlayerListener playerListener) {
        this.executorService = executorService;
        this.remoteHandler = remoteHandler;
        this.dataSerializer = jsonEntitySerializer;
        this.playerListener = playerListener;
    }

    public void addNetworkEntitySerializationConfig(NetworkEntitySerializationConfig networkEntitySerializationConfig) {
        networkEntitySerializationConfigArray.add(networkEntitySerializationConfig);
    }

    public void removeNetworkEntitySerializationConfig(NetworkEntitySerializationConfig networkEntitySerializationConfig) {
        networkEntitySerializationConfigArray.removeValue(networkEntitySerializationConfig, true);
    }

    @Override
    public boolean isComponentSerializedToClient(Component component, ClientConnection clientConnection) {
        for (NetworkEntitySerializationConfig networkEntitySerializationConfig : networkEntitySerializationConfigArray) {
            if (networkEntitySerializationConfig.isComponentSerializedToClient(component, clientConnection))
                return true;
        }
        return false;
    }

    public void connectionEstablished(WebSocketSession session) {
        executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        String username = session.getPrincipal().getName();
                        SerializingClientConnection<JsonValue> clientConnection = new SerializingClientConnection<JsonValue>(username, new WebSocketClientSession(session, networkMessageMarshaller), dataSerializer,
                                OneConnectionPerUserIntoContext.this);
                        clientConnections.put(session, clientConnection);

                        WebSocketSession oldSession = userSessions.put(username, session);
                        if (oldSession != null) {
                            remoteHandler.removeClientConnection(clientConnections.get(oldSession));
                        }

                        remoteHandler.addClientConnection(clientConnection);
                        if (oldSession != null) {
                            try {
                                oldSession.close();
                            } catch (IOException exp) {
                                // Just swallow it
                            }
                        }

                        if (playerListener != null && oldSession == null) {
                            playerListener.playerEntered(username);
                        }
                    }
                });
    }

    private JsonReader jsonReader = new JsonReader();

    public void messageReceived(WebSocketSession session, TextMessage message) {
        executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JsonValue messageJson = jsonReader.parse(message.getPayload());
                            if (messageJson.getString("type").equals(NetworkMessage.Type.EVENT.name())) {
                                EventFromClient entityEvent = (EventFromClient) dataSerializer.deserializeEntityEvent(messageJson.get("data"));
                                if (entityEvent.getClass().getAnnotation(SendToServer.class) != null) {
                                    SerializingClientConnection<JsonValue> websocketClientConnection = clientConnections.get(session);
                                    if (websocketClientConnection != null) {
                                        websocketClientConnection.eventReceived(messageJson.getString("id"), messageJson.get("data"));
                                    }
                                }
                            }
                        } catch (Exception exp) {
                            try {
                                exp.printStackTrace();
                                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary messages not supported"));
                            } catch (IOException ex) {
                                // ignore
                            }
                        }
                    }
                });
    }

    public void connectionClosed(WebSocketSession session) {
        executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        String username = session.getPrincipal().getName();
                        userSessions.remove(username, session);
                        SerializingClientConnection<JsonValue> clientConnection = clientConnections.remove(session);
                        remoteHandler.removeClientConnection(clientConnection);

                        if (playerListener != null && !userSessions.containsKey(username)) {
                            playerListener.playerLeft(username);
                        }
                    }
                });
    }

    public void closeAllConnections() {
        for (WebSocketSession session : userSessions.values()) {
            connectionClosed(session);
        }
    }
}
