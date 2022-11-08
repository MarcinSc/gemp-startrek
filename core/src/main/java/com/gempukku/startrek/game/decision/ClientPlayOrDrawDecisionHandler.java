package com.gempukku.startrek.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionDefinition;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.libgdx.network.client.ServerEntityComponent;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.game.CardStorageSystem;
import com.gempukku.startrek.game.PlayRequirements;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.card.CardFilteringSystem;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;

import java.util.Set;
import java.util.function.Consumer;

public class ClientPlayOrDrawDecisionHandler extends BaseSystem implements DecisionHandler {
    private AuthenticationHolderSystem authenticationHolderSystem;
    private ClientDecisionSystem clientDecisionSystem;
    private PlayerPositionSystem playerPositionSystem;
    private StageSystem stageSystem;
    private CardFilteringSystem cardFilteringSystem;
    private CardLookupSystem cardLookupSystem;
    private CardAbilitySystem cardAbilitySystem;
    private AmountResolverSystem amountResolverSystem;
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardStorageSystem cardStorageSystem;
    private HierarchySystem hierarchySystem;
    private SpawnSystem spawnSystem;
    private SelectionSystem selectionSystem;

    private Table table;
    private TextButton playButton;
    private TextButton drawButton;
    private TextButton passButton;
    private Array<Entity> selectionEntities = new Array<>();

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("playOrDrawDecision", this);
    }

    private void initializeUI() {
        table = new Table();
        table.setFillParent(true);

        VerticalGroup verticalGroup = new VerticalGroup();

        drawButton = new TextButton("Draw a card", stageSystem.getSkin(), UISettings.mainButtonStyle);
        drawButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        drawCard();
                    }
                });
        verticalGroup.addActor(drawButton);
        playButton = new TextButton("Play", stageSystem.getSkin(), UISettings.alternativeButtonStyle);
        playButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        playCard();
                    }
                });
        verticalGroup.addActor(playButton);
        passButton = new TextButton("Pass", stageSystem.getSkin(), UISettings.alternativeButtonStyle);
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
    public void processNewDecision(JsonValue decisionData) {
        if (table == null) {
            initializeUI();
        }
        Stage stage = stageSystem.getStage();

        String username = authenticationHolderSystem.getUsername();
        Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
        checkForDraw(playerEntity);
        enableButton(playButton, false);
        markPlayableCards(username);
        startSelection();

        stage.addActor(table);
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
        int entityId = serverEntity.getComponent(ServerEntityComponent.class).getEntityId();
        ObjectMap<String, String> parameters = new ObjectMap<>();
        parameters.put("action", "play");
        parameters.put("cardId", String.valueOf(entityId));
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

    private void markPlayableCards(String username) {
        CardFilter playFilter = PlayRequirements.createPlayRequirements(
                username, cardFilteringSystem, cardFilterResolverSystem, cardAbilitySystem);

        cardFilteringSystem.forEachCardInHand(username, new Consumer<Entity>() {
            @Override
            public void accept(Entity cardEntity) {
                if (playFilter.accepts(null, null, cardEntity)) {
                    Entity renderedCard = cardStorageSystem.findRenderedCard(cardEntity);
                    Entity selectionEntity = spawnSystem.spawnEntity("game/card-full-selection.template");
                    hierarchySystem.addHierarchy(renderedCard, selectionEntity);
                    selectionEntities.add(selectionEntity);
                }
            }
        });
    }

    private void startSelection() {
        selectionSystem.startSelection(
                new SelectionDefinition() {
                    @Override
                    public boolean isSelectionTriggered() {
                        Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
                        UserInputStateComponent inputState = userInputStateEntity.getComponent(UserInputStateComponent.class);

                        return inputState.getSignals().contains("selectToggle");
                    }

                    @Override
                    public boolean canDeselect(Set<Entity> selectedEntities, Entity selected) {
                        return hierarchySystem.getChildren(selected).iterator().hasNext();
                    }

                    @Override
                    public boolean canSelect(Set<Entity> selectedEntities, Entity newSelected) {
                        return hierarchySystem.getChildren(newSelected).iterator().hasNext();
                    }

                    @Override
                    public void selectionChanged(Set<Entity> selectedEntities) {
                        if (selectedEntities.size() == 1) {
                            Entity selected = selectedEntities.iterator().next();
                            Entity selection = null;
                            enableButton(playButton, true);
                            for (Entity selectionEntity : selectionEntities) {
                                if (hierarchySystem.getParent(selectionEntity) != selected) {
                                    world.deleteEntity(selectionEntity);
                                } else {
                                    selection = selectionEntity;
                                }
                            }
                            selectionEntities.clear();
                            selectionEntities.add(selection);
                        } else {
                            enableButton(playButton, false);
                            for (Entity selectionEntity : selectionEntities) {
                                world.deleteEntity(selectionEntity);
                            }
                            selectionEntities.clear();
                            markPlayableCards(authenticationHolderSystem.getUsername());
                        }
                    }

                    @Override
                    public String getMask() {
                        return "Selection";
                    }

                    @Override
                    public Predicate<Entity> getEntityPredicate() {
                        return new Predicate<Entity>() {
                            @Override
                            public boolean evaluate(Entity arg0) {
                                return true;
                            }
                        };
                    }
                }
        );
    }

    private void executeCleanup() {
        table.remove();
        for (Entity selectionEntity : selectionEntities) {
            world.deleteEntity(selectionEntity);
        }
        selectionEntities.clear();
        selectionSystem.finishSelection();
    }

    @Override
    protected void processSystem() {

    }
}
