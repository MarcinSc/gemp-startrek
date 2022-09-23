package com.gempukku.libgdx.network.server;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.network.DataSerializer;
import com.gempukku.libgdx.network.EventFromClient;
import com.gempukku.libgdx.network.NetworkMessage;
import com.gempukku.libgdx.network.server.config.NetworkEntitySerializationConfig;

import java.util.*;

public class SerializingClientConnection<T> implements ClientConnection {
    private final ClientSession<T> session;
    private final DataSerializer<T> dataSerializer;
    private ServerCallback serverCallback;
    private final String username;

    private final Map<Integer, Entity> entitiesCreated = new HashMap<>();
    private final Map<Integer, Entity> entitiesModified = new HashMap<>();
    private final Set<Integer> entitiesRemoved = new HashSet<>();
    private final List<NetworkMessage<T>> eventsToSend = new LinkedList<>();

    private final Bag<Component> tempComponentBag = new Bag<>();

    private final NetworkEntitySerializationConfig networkEntitySerializationConfig;

    public SerializingClientConnection(String username, ClientSession<T> session, DataSerializer<T> dataSerializer,
                                       NetworkEntitySerializationConfig networkEntitySerializationConfig) {
        this.username = username;
        this.session = session;
        this.dataSerializer = dataSerializer;
        this.networkEntitySerializationConfig = networkEntitySerializationConfig;
    }

    @Override
    public String getName() {
        return username;
    }

    public void eventReceived(int entityId, T eventData) {
        serverCallback.processEvent(username, entityId, (EventFromClient) dataSerializer.deserializeEntityEvent(eventData));
    }

    @Override
    public void entityAdded(Entity entity) {
        entitiesCreated.put(entity.getId(), entity);
    }

    @Override
    public void entityModified(Entity entity) {
        if (!entitiesCreated.containsKey(entity.getId()))
            entitiesModified.put(entity.getId(), entity);
    }

    @Override
    public void entityRemoved(int entityId) {
        if (entitiesCreated.remove(entityId) == null) {
            entitiesModified.remove(entityId);
            entitiesRemoved.add(entityId);
        }
    }

    @Override
    public void eventSent(int entityId, EntityEvent event) {
        T eventData = dataSerializer.serializeEvent(event);

        eventsToSend.add(new NetworkMessage<>(entityId, NetworkMessage.Type.EVENT, eventData));
    }

    @Override
    public void applyChanges() {
        if (entitiesRemoved.size() > 0 || entitiesModified.size() > 0
                || entitiesCreated.size() > 0 || eventsToSend.size() > 0) {
            for (int entityId : entitiesRemoved) {
                session.sendMessage(new NetworkMessage<>(entityId, NetworkMessage.Type.ENTITY_REMOVED, (T) null));
            }
            for (Entity entity : entitiesCreated.values()) {
                List<T> entityData = convertToEntityData(entity);
                session.sendMessage(new NetworkMessage<>(entity.getId(), NetworkMessage.Type.ENTITY_CREATED, entityData));
            }
            for (Entity entity : entitiesModified.values()) {
                List<T> entityData = convertToEntityData(entity);
                session.sendMessage(new NetworkMessage<>(entity.getId(), NetworkMessage.Type.ENTITY_MODIFIED, entityData));
            }
            for (NetworkMessage<T> networkMessage : eventsToSend) {
                session.sendMessage(networkMessage);
            }

            entitiesCreated.clear();
            entitiesModified.clear();
            entitiesRemoved.clear();
            eventsToSend.clear();

            session.sendMessage(new NetworkMessage<>(0, NetworkMessage.Type.APPLY_CHANGES, (T) null));
        }
    }

    @Override
    public void setServerCallback(ServerCallback serverCallback) {
        this.serverCallback = serverCallback;
    }

    @Override
    public void unsetServerCallback() {
        this.serverCallback = null;
    }

    private List<T> convertToEntityData(Entity entity) {
        tempComponentBag.clear();
        entity.getComponents(tempComponentBag);

        List<T> entityData = new LinkedList<>();

        for (Component component : tempComponentBag) {
            if (networkEntitySerializationConfig.isComponentSerializedToClient(
                    component, this)) {
                entityData.add(dataSerializer.serializeComponent(component));
            }
        }
        return entityData;
    }

    @Override
    public void forceDisconnect() {
        session.disconnect();
    }
}
