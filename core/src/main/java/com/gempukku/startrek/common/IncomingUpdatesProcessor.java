package com.gempukku.startrek.common;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.animation.AnimationDirectorSystem;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.JsonDataSerializer;
import com.gempukku.libgdx.network.client.IncomingInformationPacket;
import com.gempukku.libgdx.network.client.InformationPacketUtil;
import com.gempukku.libgdx.network.client.ReceivedUpdateFromServer;

import java.util.LinkedList;
import java.util.List;

// Just a workaround for but in Artemis
@Wire(failOnNull = false)
public class IncomingUpdatesProcessor extends BaseSystem {
    private LinkedList<List<IncomingInformationPacket<JsonValue>>> awaitingUpdates = new LinkedList<>();

    private InformationPacketUtil<JsonValue> informationPacketUtil;

    private EventSystem eventSystem;

    @Wire(failOnNull = false)
    private AnimationDirectorSystem animationDirectorSystem;

    @Override
    protected void initialize() {
        informationPacketUtil = new InformationPacketUtil<>(world,
                new JsonDataSerializer());
    }

    @EventListener
    public void incomingServerUpdates(ReceivedUpdateFromServer<JsonValue> updates, Entity entity) {
        awaitingUpdates.add(updates.getPackets());
    }

    @Override
    protected void processSystem() {
        if (!awaitingUpdates.isEmpty() &&
                (animationDirectorSystem == null || !animationDirectorSystem.isAnimating("Server"))) {
            for (IncomingInformationPacket<JsonValue> packet : awaitingUpdates.removeFirst()) {
                informationPacketUtil.applyInformationPacket(packet);
            }

            eventSystem.fireEvent(new ServerStateChanged(), null);
        }
    }
}
