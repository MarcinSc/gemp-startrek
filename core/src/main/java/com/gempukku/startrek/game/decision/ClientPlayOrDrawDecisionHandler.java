package com.gempukku.startrek.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;

public class ClientPlayOrDrawDecisionHandler extends BaseSystem implements DecisionHandler {
    private AuthenticationHolderSystem authenticationHolderSystem;
    private ClientDecisionSystem clientDecisionSystem;
    private PlayerPositionSystem playerPositionSystem;
    private StageSystem stageSystem;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("playOrDrawDecision", this);
    }

    @Override
    public void processNewDecision(JsonValue decisionData) {
        Stage stage = stageSystem.getStage();

        Entity playerEntity = playerPositionSystem.getPlayerEntity(authenticationHolderSystem.getUsername());
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        System.out.println("Deck count: " + publicStats.getDeckCount());
        System.out.println("Counter count: " + publicStats.getCounterCount());
        if (publicStats.getDeckCount() > 0 && publicStats.getCounterCount() > 0) {
            Table table = new Table();
            table.setFillParent(true);
            VerticalGroup verticalGroup = new VerticalGroup();

            TextButton drawCardButton = new TextButton("Draw a card", stageSystem.getSkin(), UISettings.mainButtonStyle);
            drawCardButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            table.remove();
                            ObjectMap<String, String> parameters = new ObjectMap<>();
                            parameters.put("action", "draw");
                            clientDecisionSystem.executeDecision(parameters);
                        }
                    });
            verticalGroup.addActor(drawCardButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));

            stage.addActor(table);
        }
    }

    @Override
    protected void processSystem() {

    }
}
