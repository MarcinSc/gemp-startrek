package com.gempukku.startrek.server.websocket;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.network.NetworkMessage;
import com.gempukku.libgdx.network.NetworkMessageMarshaller;
import com.gempukku.libgdx.network.server.ClientSession;
import com.gempukku.libgdx.template.JsonUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebSocketClientSession implements ClientSession<JsonValue>, JsonUtils.JsonConverter<JsonValue> {
    private WebSocketSession webSocketSession;
    private NetworkMessageMarshaller<JsonValue> networkMessageMarshaller;

    public WebSocketClientSession(WebSocketSession webSocketSession,
                                  NetworkMessageMarshaller<JsonValue> networkMessageMarshaller) {
        this.webSocketSession = webSocketSession;
        this.networkMessageMarshaller = networkMessageMarshaller;
    }

    @Override
    public void sendMessage(NetworkMessage<JsonValue> networkMessage) {
        try {
            String networkMessageMarshalled = networkMessageMarshaller.marshall(networkMessage);
            webSocketSession.sendMessage(new TextMessage(networkMessageMarshalled));
        } catch (Exception exp) {
            exp.printStackTrace();
            try {
                webSocketSession.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    @Override
    public void disconnect() {
        try {
            webSocketSession.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    @Override
    public JsonValue convert(JsonValue value) {
        return value;
    }
}
