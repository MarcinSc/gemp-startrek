package com.gempukku.libgdx.network.json;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.gempukku.libgdx.network.NetworkMessage;
import com.gempukku.libgdx.network.NetworkMessageMarshaller;

import java.util.LinkedList;
import java.util.List;

public class JsonValueNetworkMessageMarshaller implements NetworkMessageMarshaller<JsonValue>, JsonUtils.JsonConverter<JsonValue> {
    private boolean debug = true;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public String marshall(NetworkMessage<JsonValue> networkMessage) {
        JsonValue message = new JsonValue(JsonValue.ValueType.object);
        message.addChild("id", new JsonValue(networkMessage.getEntityId()));
        message.addChild("type", new JsonValue(networkMessage.getType().name()));
        switch (networkMessage.getType()) {
            case EVENT:
                message.addChild("data", new JsonValue(networkMessage.getPayloadList().get(0).toJson(JsonWriter.OutputType.json)));
                break;
            case ENTITY_CREATED:
            case ENTITY_MODIFIED:
                message.addChild("data", JsonUtils.convertToJsonArray(networkMessage.getPayloadList(), this));
                break;
            case ENTITY_REMOVED:
            case APPLY_CHANGES:
                break;
        }
        String result = message.toJson(JsonWriter.OutputType.json);
        if (debug) {
            System.out.println("Sending network message:");
            System.out.println(result);
        }
        return result;
    }

    @Override
    public NetworkMessage<JsonValue> unmarshall(JsonValue json) {
        if (debug) {
            System.out.println("Received network message:");
            System.out.println(json.toJson(JsonWriter.OutputType.json));
        }
        try {
            String id = json.getString("id");
            NetworkMessage.Type type = NetworkMessage.Type.valueOf(json.getString("type"));
            switch (type) {
                case EVENT:
                    JsonValue eventData = json.get("data");
                    return new NetworkMessage<>(id, NetworkMessage.Type.EVENT, eventData);
                case ENTITY_CREATED:
                case ENTITY_MODIFIED:
                    JsonValue entityData = json.get("data");
                    List<JsonValue> entityComponents = new LinkedList<>();
                    for (JsonValue entityDatum : entityData) {
                        entityComponents.add(entityDatum);
                    }
                    return new NetworkMessage<>(id, type, entityComponents);
                case ENTITY_REMOVED:
                case APPLY_CHANGES:
                    return new NetworkMessage<>(id, type, null);
            }
            return null;
        } catch (Exception exp) {
            return null;
        }
    }

    @Override
    public JsonValue convert(JsonValue value) {
        return value;
    }
}
