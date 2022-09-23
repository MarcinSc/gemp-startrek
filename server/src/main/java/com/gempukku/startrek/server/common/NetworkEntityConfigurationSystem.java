package com.gempukku.startrek.server.common;

import com.artemis.BaseSystem;
import com.gempukku.libgdx.network.server.RemoteEntityManagerHandler;
import com.gempukku.libgdx.network.server.config.annotation.ReplicateToClientsConfig;
import com.gempukku.libgdx.network.server.config.annotation.SendEventToClientsConfig;

public class NetworkEntityConfigurationSystem extends BaseSystem {
    private RemoteEntityManagerHandler remoteEntityManagerHandler;

    @Override
    protected void initialize() {
        remoteEntityManagerHandler.addNetworkEntityConfig(
                new ReplicateToClientsConfig());
        remoteEntityManagerHandler.addNetworkEntityConfig(
                new SendEventToClientsConfig());
    }

    @Override
    protected void processSystem() {

    }
}
