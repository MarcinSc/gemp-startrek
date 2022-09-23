package com.gempukku.libgdx.network.server;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EntityEvent;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.event.RawEventListener;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.libgdx.network.EventFromClient;
import com.gempukku.libgdx.network.server.config.NetworkEntityConfig;
import com.gempukku.libgdx.network.server.config.annotation.SendToClients;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.Map;

public class RemoteEntityManagerHandler extends BaseSystem implements RemoteHandler {
    private Map<String, ClientConnection> clientConnectionMap = new HashMap<>();
    private Multimap<ClientConnection, Integer> clientTrackingEntities = HashMultimap.create();

    private EventSystem eventSystem;
    private EntitySubscription allEntitiesSubscription;
    private Array<NetworkEntityConfig> networkEntityConfigArray = new Array<>();

    @Override
    public void initialize() {
        allEntitiesSubscription = world.getAspectSubscriptionManager().get(Aspect.all());
        allEntitiesSubscription.addSubscriptionListener(
                new EntitySubscription.SubscriptionListener() {
                    @Override
                    public void inserted(IntBag entities) {

                    }

                    @Override
                    public void removed(IntBag entities) {
                        for (int i = 0, s = entities.size(); s > i; i++) {
                            entityRemoved(entities.get(i));
                        }
                    }
                }
        );
        eventSystem.addRawEventListener(
                new RawEventListener() {
                    @Override
                    public void eventDispatched(EntityEvent event, Entity entity) {
                        if (event.getClass().getAnnotation(SendToClients.class) != null) {
                            for (ClientConnection clientConnection : clientConnectionMap.values()) {
                                if (clientTracksEntity(clientConnection, entity.getId()))
                                    clientConnection.eventSent(entity.getId(), event);
                            }
                        }
                    }
                });
    }

    public void addNetworkEntityConfig(NetworkEntityConfig networkEntityConfig) {
        networkEntityConfigArray.add(networkEntityConfig);
    }

    public void removeNetworkEntityConfig(NetworkEntityConfig networkEntityConfig) {
        networkEntityConfigArray.removeValue(networkEntityConfig, true);
    }

    @EventListener
    public void entityUpdated(EntityUpdated entityUpdated, Entity entity) {
        boolean replicateToAllClients = hasToReplicateToAllClients(entity);

        int entityId = entity.getId();
        for (Map.Entry<String, ClientConnection> clientConnectionEntry : clientConnectionMap.entrySet()) {
            ClientConnection clientConnection = clientConnectionEntry.getValue();
            if (replicateToAllClients || hasToReplicateToClient(entity, clientConnection)) {
                if (clientTracksEntity(clientConnection, entityId)) {
                    clientConnection.entityModified(entity);
                } else {
                    clientTrackingEntities.put(clientConnection, entityId);
                    clientConnection.entityAdded(entity);
                }
            } else if (clientTracksEntity(clientConnection, entityId)) {
                clientConnection.entityRemoved(entityId);
                clientTrackingEntities.remove(clientConnection, entityId);
            }
        }
    }

    private boolean hasToReplicateToAllClients(Entity entity) {
        for (NetworkEntityConfig networkEntityConfig : networkEntityConfigArray) {
            if (networkEntityConfig.isEntitySentToAll(entity))
                return true;
        }
        return false;
    }

    private void entityRemoved(int entityId) {
        for (ClientConnection clientConnection : clientConnectionMap.values()) {
            if (clientTracksEntity(clientConnection, entityId)) {
                clientConnection.entityRemoved(entityId);
                clientTrackingEntities.remove(clientConnection, entityId);
            }
        }
    }

    private void broadcastApplyChanges() {
        for (ClientConnection value : clientConnectionMap.values()) {
            value.applyChanges();
        }
    }

    @Override
    public void addClientConnection(final ClientConnection clientConnection) {
        String clientName = clientConnection.getName();

        ClientConnection oldConnection = clientConnectionMap.get(clientName);
        if (oldConnection != null) {
            oldConnection.forceDisconnect();
            clientTrackingEntities.removeAll(oldConnection);
        }
        clientConnectionMap.put(clientName, clientConnection);

        clientConnection.setServerCallback(
                new ServerCallback() {
                    @Override
                    public void processEvent(String fromUser, int entityId, EventFromClient event) {
                        Entity trackedEntity = findTrackedEntity(entityId);
                        if (trackedEntity != null) {
                            event.setOrigin(fromUser);
                            eventSystem.fireEvent(event, trackedEntity);
                        }
                    }

                    private Entity findTrackedEntity(int entityId) {
                        if (clientTracksEntity(clientConnection, entityId))
                            return world.getEntity(entityId);
                        return null;
                    }
                });

        IntBag entities = allEntitiesSubscription.getEntities();
        for (int i = 0, s = entities.size(); s > i; i++) {
            int entityId = entities.get(i);
            Entity entity = world.getEntity(entityId);
            if (hasToReplicateToAllClients(entity) || hasToReplicateToClient(entity, clientConnection)) {
                clientConnection.entityAdded(entity);
                clientTrackingEntities.put(clientConnection, entityId);
            }
        }

        clientConnection.applyChanges();
    }

    private boolean hasToReplicateToClient(Entity entity, ClientConnection clientConnection) {
        for (NetworkEntityConfig networkEntityConfig : networkEntityConfigArray) {
            if (networkEntityConfig.isEntitySentToClient(entity, clientConnection))
                return true;
        }

        return false;
    }

    private boolean clientTracksEntity(ClientConnection clientConnection, int entityId) {
        return clientTrackingEntities.containsEntry(clientConnection, entityId);
    }

    @Override
    public void removeClientConnection(ClientConnection clientConnection) {
        clientConnection.unsetServerCallback();
        clientTrackingEntities.removeAll(clientConnection);
        clientConnectionMap.remove(clientConnection.getName());
    }

    @Override
    public void disconnectAllClients() {
        for (ClientConnection clientConnection : clientConnectionMap.values()) {
            clientConnection.unsetServerCallback();
            clientConnection.forceDisconnect();
        }
    }

    @Override
    protected void processSystem() {
        broadcastApplyChanges();
    }
}
