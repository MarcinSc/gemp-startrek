package com.gempukku.startrek.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.libgdx.network.client.ServerEntityComponent;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.game.PlayRequirements;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.render.CardRenderingSystem;
import com.gempukku.startrek.game.render.PromptRenderingSystem;

public class ClientPlayOrDrawDecisionHandler extends BaseSystem implements DecisionHandler {
    private AuthenticationHolderSystem authenticationHolderSystem;
    private ClientDecisionSystem clientDecisionSystem;
    private PlayerPositionSystem playerPositionSystem;
    private StageSystem stageSystem;
    private CardFilteringSystem cardFilteringSystem;
    private CardLookupSystem cardLookupSystem;
    private CardAbilitySystem cardAbilitySystem;
    private AmountResolverSystem amountResolverSystem;
    private CardRenderingSystem cardRenderingSystem;
    private HierarchySystem hierarchySystem;
    private SpawnSystem spawnSystem;
    private SelectionSystem selectionSystem;
    private PromptRenderingSystem promptRenderingSystem;

    private Table table;
    private TextButton playButton;
    private TextButton drawButton;
    private TextButton passButton;

    private SelectionState selectionState;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("playOrDrawDecision", this);
    }

    private void initializeForDecisions() {
        Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
        CardFilter playRequirementsFilter = PlayRequirements.createPlayRequirements(authenticationHolderSystem.getUsername(),
                cardFilteringSystem, cardAbilitySystem);

        selectionState = new SelectionState(world, userInputStateEntity, "hand", playRequirementsFilter,
                new SelectionCallback() {
                    @Override
                    public void selectionChanged(ObjectSet<Entity> selected) {
                        enableButton(playButton, selected.size == 1);
                    }
                });

        table = new Table();
        table.setFillParent(true);

        VerticalGroup verticalGroup = new VerticalGroup();

        drawButton = new TextButton("Draw a card", stageSystem.getSkin(), UISettings.mainButtonStyle) {
            @Override
            public float getPrefWidth() {
                return 200;
            }
        };
        drawButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        drawCard();
                    }
                });
        verticalGroup.addActor(drawButton);
        playButton = new TextButton("Play", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
            @Override
            public float getPrefWidth() {
                return 200;
            }
        };
        playButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        playCard();
                    }
                });
        verticalGroup.addActor(playButton);
        passButton = new TextButton("Pass", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
            @Override
            public float getPrefWidth() {
                return 200;
            }
        };
        passButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        pass();
                    }
                });
        verticalGroup.addActor(passButton);

        table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
    }

    @Override
    public void processNewDecision(ObjectMap<String, String> decisionData) {
        if (table == null) {
            initializeForDecisions();
        }
        Stage stage = stageSystem.getStage();

        String username = authenticationHolderSystem.getUsername();
        Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
        checkForDraw(playerEntity);

        selectionState.markSelectableCards();
        selectionSystem.startSelection(selectionState);

        enableButton(playButton, false);

        stage.addActor(table);

        promptRenderingSystem.setPrompt("Play and Draw Cards");
    }

    private void drawCard() {
        ObjectMap<String, String> parameters = new ObjectMap<>();
        parameters.put("action", "draw");
        executeCleanup();
        clientDecisionSystem.executeDecision(parameters);
    }

    private void pass() {
        ObjectMap<String, String> parameters = new ObjectMap<>();
        parameters.put("action", "pass");
        executeCleanup();
        clientDecisionSystem.executeDecision(parameters);
    }

    private void playCard() {
        Entity selected = selectionSystem.getSelectedEntities().iterator().next();
        int serverEntityId = selected.getComponent(ServerCardReferenceComponent.class).getEntityId();
        Entity serverEntity = world.getEntity(serverEntityId);
        String entityId = serverEntity.getComponent(ServerEntityComponent.class).getEntityId();
        ObjectMap<String, String> parameters = new ObjectMap<>();
        parameters.put("action", "play");
        parameters.put("cardId", entityId);
        executeCleanup();
        clientDecisionSystem.executeDecision(parameters);
    }

    private void enableButton(Button button, boolean enabled) {
        button.setDisabled(!enabled);
        button.setTouchable(enabled ? Touchable.enabled : Touchable.disabled);
    }

    private void checkForDraw(Entity playerEntity) {
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        boolean drawPossible = publicStats.getDeckCount() > 0 && publicStats.getCounterCount() > 0;
        enableButton(drawButton, drawPossible);
        boolean passPossible = !drawPossible;
        enableButton(passButton, passPossible);
    }

    private void executeCleanup() {
        table.remove();
        selectionState.cleanup();
        selectionSystem.finishSelection();

        promptRenderingSystem.removePrompt();
    }

    @Override
    protected void processSystem() {

    }
}
