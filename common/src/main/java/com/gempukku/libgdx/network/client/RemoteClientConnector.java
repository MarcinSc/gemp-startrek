package com.gempukku.libgdx.network.client;

public interface RemoteClientConnector {
    void connectToServer(String host, int port, String address, String player, String authenticationToken);

    void disconnectFromServer();
}
