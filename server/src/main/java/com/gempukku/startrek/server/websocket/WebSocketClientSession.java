package com.gempukku.startrek.server.websocket;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.gempukku.libgdx.network.NetworkMessage;
import com.gempukku.libgdx.network.server.ClientSession;
import com.gempukku.libgdx.template.JsonUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebSocketClientSession implements ClientSession<JsonValue>, JsonUtils.JsonConverter<JsonValue> {
    private WebSocketSession webSocketSession;

    public WebSocketClientSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    @Override
    public void sendMessage(NetworkMessage<JsonValue> networkMessage) {
        try {
            JsonValue message = new JsonValue(JsonValue.ValueType.object);
            message.addChild("id", new JsonValue(networkMessage.getEntityId()));
            message.addChild("type", new JsonValue(networkMessage.getType().name()));
            if (networkMessage.getType() == NetworkMessage.Type.EVENT) {
                message.addChild("data", new JsonValue(networkMessage.getPayloadList().get(0).toJson(JsonWriter.OutputType.json)));
            } else if (networkMessage.getType() == NetworkMessage.Type.ENTITY_CREATED
                    || networkMessage.getType() == NetworkMessage.Type.ENTITY_MODIFIED) {
                message.addChild("data", JsonUtils.convertToJsonArray(networkMessage.getPayloadList(), this));
            }
            webSocketSession.sendMessage(new TextMessage(message.toJson(JsonWriter.OutputType.json)));
        } catch (Exception exp) {
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
