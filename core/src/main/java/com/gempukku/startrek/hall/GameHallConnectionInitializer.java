package com.gempukku.startrek.hall;

import com.artemis.BaseSystem;
import com.gempukku.libgdx.network.client.WebsocketRemoteClientConnector;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.ConnectionParamSystem;
import com.gempukku.startrek.common.GameSceneSystem;
import com.gempukku.startrek.login.LoginGameScene;

public class GameHallConnectionInitializer extends BaseSystem {
    private WebsocketRemoteClientConnector remoteClientConnector;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private ConnectionParamSystem connectionParamSystem;
    private GameSceneSystem gameSceneManager;

    private boolean initialized;

    @Override
    protected void processSystem() {
        if (!initialized) {
            try {
                remoteClientConnector.connectToServer(connectionParamSystem.getServerHost(), connectionParamSystem.getServerPort(),
                        connectionParamSystem.getHallUrl(), authenticationHolderSystem.getAuthenticationToken());
                initialized = true;
            } catch (Exception exp) {
                gameSceneManager.setNextGameScene(new LoginGameScene());
            }
        }
    }

    @Override
    protected void dispose() {
        if (initialized) {
            remoteClientConnector.disconnectFromServer();
        }
    }
}
