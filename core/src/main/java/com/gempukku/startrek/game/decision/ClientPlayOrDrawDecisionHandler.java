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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionDefinition;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.game.CardStorageSystem;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ability.ClientCardAbilitySystem;
import com.gempukku.startrek.game.ability.HeadquarterRequirements;
import com.gempukku.startrek.game.amount.AmountResolverSystem;
import com.gempukku.startrek.game.card.CardFilteringSystem;
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
    private ClientCardAbilitySystem clientCardAbilitySystem;
    private AmountResolverSystem amountResolverSystem;
    private CardFilterResolverSystem cardFilterResolverSystem;
    private CardStorageSystem cardStorageSystem;
    private HierarchySystem hierarchySystem;
    private SpawnSystem spawnSystem;
    private SelectionSystem selectionSystem;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("playOrDrawDecision", this);
    }

    @Override
    public void processNewDecision(JsonValue decisionData) {
        Stage stage = stageSystem.getStage();

        Table table = new Table();
        table.setFillParent(true);
        Array<Runnable> decisionCleanup = new Array<>();
        decisionCleanup.add(
                new Runnable() {
                    @Override
                    public void run() {
                        table.remove();
                    }
                });

        VerticalGroup verticalGroup = new VerticalGroup();
        String username = authenticationHolderSystem.getUsername();
        Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
        checkForDraw(table, verticalGroup, playerEntity, decisionCleanup);
        markPlayableCards(username, decisionCleanup);

        table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));

        stage.addActor(table);
    }

    private void checkForDraw(Table table, VerticalGroup verticalGroup, Entity playerEntity,
                              Array<Runnable> decisionCleanup) {
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        if (publicStats.getDeckCount() > 0 && publicStats.getCounterCount() > 0) {
            TextButton drawCardButton = new TextButton("Draw a card", stageSystem.getSkin(), UISettings.mainButtonStyle);
            drawCardButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            executeCleanup(decisionCleanup);
                            ObjectMap<String, String> parameters = new ObjectMap<>();
                            parameters.put("action", "draw");
                            clientDecisionSystem.executeDecision(parameters);
                        }
                    });
            verticalGroup.addActor(drawCardButton);
        } else {
            TextButton passButton = new TextButton("Pass", stageSystem.getSkin(), UISettings.mainButtonStyle);
            passButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            executeCleanup(decisionCleanup);
                            ObjectMap<String, String> parameters = new ObjectMap<>();
                            parameters.put("action", "pass");
                            clientDecisionSystem.executeDecision(parameters);
                        }
                    });
            verticalGroup.addActor(passButton);
        }
    }

    private void markPlayableCards(String username, Array<Runnable> decisionCleanup) {
        Entity playerHeadquarter = cardFilteringSystem.findFirstCardInPlay("missionType(Headquarters),owner(username(" + username + "))");
        CardFilter headquarterRequirements = clientCardAbilitySystem.getClientCardAbility(playerHeadquarter, HeadquarterRequirements.class).getCardFilter();
        CardFilter cardFilter = cardFilterResolverSystem.resolveCardFilter(
                "or(type(Personnel),type(Ship),type(Equipment)),"
                        + "playable,"
                        + "condition(lessOrEqual(costToPlay,counterCount(username(" + username + "))))");

        cardFilteringSystem.forEachCardInHand(username, new Consumer<Entity>() {
            @Override
            public void accept(Entity cardEntity) {
                if (headquarterRequirements.accepts(null, null, cardEntity)
                        && cardFilter.accepts(null, null, cardEntity)) {
                    Entity renderedCard = cardStorageSystem.findRenderedCard(cardEntity);
                    Entity selectionEntity = spawnSystem.spawnEntity("game/card-full-selection.template");
                    hierarchySystem.addHierarchy(renderedCard, selectionEntity);
                    decisionCleanup.add(
                            new Runnable() {
                                @Override
                                public void run() {
                                    world.deleteEntity(selectionEntity);
                                }
                            }
                    );
                }
            }
        });

        selectionSystem.startSelection(
                new SelectionDefinition() {
                    @Override
                    public boolean isSelectionTriggered() {
                        return true;
                    }

                    @Override
                    public boolean canDeselect(Set<Entity> selectedEntities, Entity selected) {
                        return true;
                    }

                    @Override
                    public boolean canSelect(Set<Entity> selectedEntities, Entity newSelected) {
                        return true;
                    }

                    @Override
                    public void selectionChanged(Set<Entity> selectedEntities) {
                        
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

    private void executeCleanup(Array<Runnable> decisionCleanup) {
        for (Runnable runnable : decisionCleanup) {
            runnable.run();
        }
    }

    @Override
    protected void processSystem() {

    }
}
