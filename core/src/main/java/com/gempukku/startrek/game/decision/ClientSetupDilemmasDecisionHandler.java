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
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.common.UISettings;

public class ClientSetupDilemmasDecisionHandler extends BaseSystem implements DecisionHandler {
    private ClientDecisionSystem clientDecisionSystem;
    private StageSystem stageSystem;

    private Table table;
    private TextButton finishedButton;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("setupDilemmas", this);
    }

    private void initializeForDecisions() {
        Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);

        table = new Table();
        table.setFillParent(true);

        VerticalGroup verticalGroup = new VerticalGroup();

        finishedButton = new TextButton("Finished", stageSystem.getSkin(), UISettings.mainButtonStyle) {
            @Override
            public float getPrefWidth() {
                return 200;
            }
        };
        finishedButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        finishedSetup();
                    }
                });
        verticalGroup.addActor(finishedButton);

        table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
    }

    @Override
    public void processNewDecision(ObjectMap<String, String> decisionData) {
        if (table == null) {
            initializeForDecisions();
        }
        Stage stage = stageSystem.getStage();

        String[] cardIds = StringUtils.split(decisionData.get("dilemmaCardIds"));
        int costCount = Integer.parseInt(decisionData.get("costCount"));


        stage.addActor(table);
    }

    private void finishedSetup() {
        ObjectMap<String, String> parameters = new ObjectMap<>();
        parameters.put("action", "pass");
        executeCleanup();
        clientDecisionSystem.executeDecision(parameters);
    }

    private void executeCleanup() {
        table.remove();
    }

    @Override
    protected void processSystem() {

    }
}
