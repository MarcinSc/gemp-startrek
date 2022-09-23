package com.gempukku.startrek.common;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.JsonDataSerializer;
import com.gempukku.libgdx.network.client.IncomingInformationPacket;
import com.gempukku.libgdx.network.client.InformationPacketUtil;
import com.gempukku.libgdx.network.client.ReceivedUpdateFromServer;

public class IncomingUpdatesProcessor extends BaseSystem {
    private InformationPacketUtil<JsonValue> informationPacketUtil;

    private EventSystem eventSystem;
    private boolean stateChanged = false;

    @Override
    protected void initialize() {
        informationPacketUtil = new InformationPacketUtil<>(world,
                new JsonDataSerializer());
    }

    @EventListener
    public void incomingServerUpdates(ReceivedUpdateFromServer<JsonValue> updates, Entity entity) {
        for (IncomingInformationPacket<JsonValue> packet : updates.getPackets()) {
            informationPacketUtil.applyInformationPacket(packet);
        }

        stateChanged = true;
    }

    @Override
    protected void processSystem() {
        if (stateChanged) {
            stateChanged = false;
            eventSystem.fireEvent(new ServerStateChanged(), null);
        }
    }
}
