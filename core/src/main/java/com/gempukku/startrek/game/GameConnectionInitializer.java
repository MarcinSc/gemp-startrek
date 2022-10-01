
package com.gempukku.startrek.game;

import com.artemis.BaseSystem;
import com.gempukku.libgdx.network.client.WebsocketRemoteClientConnector;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.ConnectionParamSystem;
import com.gempukku.startrek.common.GameSceneSystem;
import com.gempukku.startrek.login.LoginGameScene;

public class GameConnectionInitializer extends BaseSystem {
    private WebsocketRemoteClientConnector remoteClientConnector;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private ConnectionParamSystem connectionParamSystem;
    private GameSceneSystem gameSceneManager;

    private boolean initialized;

    @Override
    protected void processSystem() {
        if (!initialized) {
            try {
                String gameId = LazyEntityUtil.findEntityWithComponent(world, StarTrekGameComponent.class).getComponent(StarTrekGameComponent.class).getGameId();
                remoteClientConnector.connectToServer(connectionParamSystem.getServerHost(), connectionParamSystem.getServerPort(),
                        connectionParamSystem.getGameUrl(gameId), authenticationHolderSystem.getAuthenticationToken());
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
