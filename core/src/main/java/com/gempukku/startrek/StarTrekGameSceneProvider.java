package com.gempukku.startrek;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.gempukku.libgdx.lib.artemis.camera.ScreenResized;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.lib.artemis.input.InputProcessorSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.GameSceneSystem;
import com.gempukku.startrek.login.LoginGameScene;
import com.github.czyzby.websocket.CommonWebSockets;

public class StarTrekGameSceneProvider implements GameSceneProvider {
    private AuthenticationHolderSystem authenticationHolderSystem;
    private GameSceneSystem gameSceneSystem;

    private GameScene currentScene;
    private World currentSceneWorld;

    @Override
    public void startup() {
        CommonWebSockets.initiate();

        authenticationHolderSystem = new AuthenticationHolderSystem();
        gameSceneSystem = new GameSceneSystem();

        currentScene = new LoginGameScene();
        currentSceneWorld = createCurrentSceneWorld();
    }

    private World createCurrentSceneWorld() {
        WorldConfigurationBuilder worldConfigurationBuilder = new WorldConfigurationBuilder();
        worldConfigurationBuilder.with(authenticationHolderSystem, gameSceneSystem);
        return currentScene.visit(new WorldCreatingVisitor(worldConfigurationBuilder));
    }

    @Override
    public void setActive() {
        InputProcessorSystem inputProcessor = currentSceneWorld.getSystem(InputProcessorSystem.class);
        inputProcessor.setupProcessing();
    }

    @Override
    public void setInactive() {

    }

    @Override
    public void resize(int width, int height) {
        EventSystem eventSystem = currentSceneWorld.getSystem(EventSystem.class);
        eventSystem.fireEvent(new ScreenResized(width, height), null);
    }

    @Override
    public void processContext() {
        GameScene gameScene = gameSceneSystem.consumeNextGameScene();
        if (gameScene != null) {
            System.out.println("Transitioning to scene: " + gameScene.getClass().getSimpleName());
            disposeCurrentWorld();
            currentScene = gameScene;
            currentSceneWorld = createCurrentSceneWorld();
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            setActive();
        }
        currentSceneWorld.setDelta(Gdx.graphics.getDeltaTime());
        currentSceneWorld.process();
    }

    @Override
    public void shutdown() {
        disposeCurrentWorld();
    }

    private void disposeCurrentWorld() {
        currentSceneWorld.dispose();
    }
}
