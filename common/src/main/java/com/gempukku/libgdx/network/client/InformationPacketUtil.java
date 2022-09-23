package com.gempukku.libgdx.network.client;


import com.artemis.*;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.IntMap;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.DataSerializer;

public class InformationPacketUtil<T> {
    private final World world;
    private final EventSystem eventSystem;
    private final ComponentMapper<ServerEntityComponent> serverEntityMapper;
    private final DataSerializer<T> dataSerializer;
    private final IntMap<Entity> serverEntityMap = new IntMap<>();

    private final Bag<Component> tempComponentBag = new Bag<>();

    public InformationPacketUtil(World world, DataSerializer<T> dataSerializer) {
        this.world = world;
        this.eventSystem = world.getSystem(EventSystem.class);
        serverEntityMapper = world.getMapper(ServerEntityComponent.class);
        this.dataSerializer = dataSerializer;
        world.getAspectSubscriptionManager().get(Aspect.all(ServerEntityComponent.class)).
                addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; i++) {
                                    entityAdded(entities.get(i));
                                }
                            }

                            @Override
                            public void removed(IntBag entities) {
                                for (int i = 0, s = entities.size(); s > i; i++) {
                                    entityRemoved(entities.get(i));
                                }
                            }
                        });
    }

    private void entityAdded(int i) {
        Entity entity = world.getEntity(i);
        int serverEntityId = serverEntityMapper.get(entity).getEntityId();
        serverEntityMap.put(serverEntityId, entity);
    }

    private void entityRemoved(int i) {
        int serverEntityId = serverEntityMapper.get(i).getEntityId();
        serverEntityMap.remove(serverEntityId);
    }

    public void applyInformationPacket(IncomingInformationPacket<T> packet) {
        if (packet.getType() == IncomingInformationPacket.Type.EVENT) {
            int entityId = packet.getEntityId();
            Entity entity = serverEntityMap.get(entityId);
            if (entity != null) {
                eventSystem.fireEvent(packet.getEvent(), entity);
            }
        } else if (packet.getType() == IncomingInformationPacket.Type.CREATE_ENTITY) {
            Entity entity = world.createEntity();
            ServerEntityComponent serverEntityComponent = serverEntityMapper.create(entity);
            serverEntityComponent.setEntityId(packet.getEntityId());

            for (T entityDatum : packet.getEntityData()) {
                dataSerializer.deserializeComponent(entity, world, entityDatum);
            }
        } else if (packet.getType() == IncomingInformationPacket.Type.MODIFY_ENTITY) {
            Entity entity = serverEntityMap.get(packet.getEntityId());
            if (entity != null) {
                tempComponentBag.clear();
                entity.getComponents(tempComponentBag);
                for (Component component : tempComponentBag) {
                    if (component.getClass().getAnnotation(PreserveComponent.class) == null && !(component instanceof ServerEntityComponent)) {
                        removeComponent(entity, component);
                    }
                }

                for (T entityDatum : packet.getEntityData()) {
                    dataSerializer.deserializeComponent(entity, world, entityDatum);
                }
            }
        } else if (packet.getType() == IncomingInformationPacket.Type.DESTROY_ENTITY) {
            Entity entity = serverEntityMap.get(packet.getEntityId());
            if (entity != null) {
                world.deleteEntity(entity);
            }
        }
    }

    private void removeComponent(Entity entity, Component component) {
        ComponentMapper<? extends Component> mapper = world.getMapper(component.getClass());
        mapper.remove(entity);
    }
}
