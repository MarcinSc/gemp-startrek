package com.gempukku.startrek.hall;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.libgdx.network.client.ConnectionLost;
import com.gempukku.startrek.common.GameSceneSystem;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.login.LoginGameScene;

public class HallConnectionLostHandling extends BaseSystem {
    private StageSystem stageSystem;
    private GameSceneSystem gameSceneSystem;

    @EventListener
    public void connectionLost(ConnectionLost connectionLost, Entity entity) {
        Stage stage = stageSystem.getStage();
        Skin skin = stageSystem.getSkin();

        final Window window = new Window("Connection lost", skin);
        window.setModal(true);
        Label question = new Label("Connection to the server was lost.\nWould you like to?", skin, "title-plain");
        question.setAlignment(Align.center);
        window.add(question).grow().colspan(2);
        window.row();

        TextButton hallButton = new TextButton("Reconnect", skin, UISettings.mainButtonStyle);
        hallButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        gameSceneSystem.setNextGameScene(new HallGameScene());
                    }
                });
        TextButton loginButton = new TextButton("Exit to login", skin, UISettings.alternativeButtonStyle);
        loginButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        gameSceneSystem.setNextGameScene(new LoginGameScene());
                    }
                });

        window.add(hallButton);
        window.add(loginButton);
        window.row();

        window.setSize(450, 200);

        stage.addActor(window);

        window.setPosition((stage.getWidth() - window.getWidth()) / 2, (stage.getHeight() - window.getHeight()) / 2);
    }

    @Override
    protected void processSystem() {

    }
}
