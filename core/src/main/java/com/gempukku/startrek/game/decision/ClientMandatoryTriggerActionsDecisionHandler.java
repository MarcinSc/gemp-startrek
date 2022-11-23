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
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.libgdx.network.client.ServerEntityComponent;
import com.gempukku.libgdx.network.client.ServerEntitySystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.TriggerRequirements;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.render.PromptRenderingSystem;

public class ClientMandatoryTriggerActionsDecisionHandler extends BaseSystem implements DecisionHandler {
    private ClientDecisionSystem clientDecisionSystem;
    private CardFilteringSystem cardFilteringSystem;
    private CardAbilitySystem cardAbilitySystem;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private StageSystem stageSystem;
    private SelectionSystem selectionSystem;
    private ConditionResolverSystem conditionResolverSystem;
    private ServerEntitySystem serverEntitySystem;
    private PromptRenderingSystem promptRenderingSystem;

    private Table table;
    private TextButton useButton;
    private TextButton passButton;

    private SelectionState selectionState;
    private String triggerType;
    private Memory memory;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("mandatoryTriggerActions", this);
    }

    private void initializeForDecision() {
        table = new Table();
        table.setFillParent(true);

        VerticalGroup verticalGroup = new VerticalGroup();

        useButton = new TextButton("Use", stageSystem.getSkin(), UISettings.mainButtonStyle) {
            @Override
            public float getPrefWidth() {
                return 200;
            }
        };
        useButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        useCard();
                    }
                });
        verticalGroup.addActor(useButton);
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
            initializeForDecision();
        }

        initializeSelectionState(decisionData);
        selectionState.markSelectableCards();

        if (!selectionState.hasSelectableEntities()) {
            pass();
        } else {
            Stage stage = stageSystem.getStage();

            selectionSystem.startSelection(selectionState);

            enableButton(useButton, false);
            enableButton(passButton, !selectionState.hasSelectableEntities());

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose mandatory trigger");
        }
    }

    private void initializeSelectionState(ObjectMap<String, String> decisionData) {
        String sourceId = decisionData.get("sourceId");
        triggerType = decisionData.get("triggerType");
        String usedIds = decisionData.get("usedIds", "");

        Entity sourceEntity = null;
        if (sourceId != null)
            sourceEntity = serverEntitySystem.findEntity(sourceId);

        memory = new Memory(decisionData);

        Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
        CardFilter triggerFilter = TriggerRequirements.createMandatoryTriggerRequirements(
                authenticationHolderSystem.getUsername(), triggerType, usedIds,
                cardFilteringSystem);
        selectionState = new SelectionState(world, userInputStateEntity, triggerFilter,
                sourceEntity, memory,
                new SelectionCallback() {
                    @Override
                    public void selectionChanged(ObjectSet<Entity> selected) {
                        enableButton(useButton, selected.size == 1);
                    }
                });
    }

    private void enableButton(Button button, boolean enabled) {
        button.setDisabled(!enabled);
        button.setTouchable(enabled ? Touchable.enabled : Touchable.disabled);
    }

    private void useCard() {
        Entity selected = selectionSystem.getSelectedEntities().iterator().next();
        int serverEntityId = selected.getComponent(ServerCardReferenceComponent.class).getEntityId();
        Entity usedCardEntity = world.getEntity(serverEntityId);
        String entityId = usedCardEntity.getComponent(ServerEntityComponent.class).getEntityId();

        int triggerIndex = TriggerRequirements.findUsableTriggerIndex(usedCardEntity, triggerType, false, memory,
                cardAbilitySystem, conditionResolverSystem);

        ObjectMap<String, String> parameters = new ObjectMap<>();
        parameters.put("action", "use");
        parameters.put("cardId", entityId);
        parameters.put("triggerIndex", String.valueOf(triggerIndex));
        executeCleanup();
        clientDecisionSystem.executeDecision(parameters);
    }

    private void pass() {
        ObjectMap<String, String> parameters = new ObjectMap<>();
        parameters.put("action", "pass");
        executeCleanup();
        clientDecisionSystem.executeDecision(parameters);
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
