package com.gempukku.startrek.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.graph.artemis.selection.SelectionSystem;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.libgdx.network.client.ServerEntityComponent;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.MissionType;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.OrderMoveRequirements;
import com.gempukku.startrek.game.OrderRequirements;
import com.gempukku.startrek.game.ability.CardAbilitySystem;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.condition.ConditionResolverSystem;
import com.gempukku.startrek.game.filter.AndCardFilter;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.render.PromptRenderingSystem;
import com.gempukku.startrek.game.zone.CardInPlayComponent;

public class ClientExecuteOrdersDecisionHandler extends BaseSystem implements DecisionHandler {
    private ClientDecisionSystem clientDecisionSystem;
    private StageSystem stageSystem;
    private SelectionSystem selectionSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private CardFilteringSystem cardFilteringSystem;
    private CardAbilitySystem cardAbilitySystem;
    private PlayerResolverSystem playerResolverSystem;
    private ConditionResolverSystem conditionResolverSystem;
    private PromptRenderingSystem promptRenderingSystem;
    private CardLookupSystem cardLookupSystem;

    private DecisionInterface decisionInterface;

    private MainDecisionInterface mainDecisionInterface;
    private AttemptMissionInterface attemptMissionInterface;
    private MoveShipSelectionInterface moveShipSelectionInterface;
    private BeamSelectionInterface beamSelectionInterface;
    private BeamFromMissionChooseShipInterface beamFromMissionChooseShipInterface;
    private BeamToMissionChooseShipInterface beamToMissionChooseShipInterface;
    private BeamBetweenShipsChooseFirstShipInterface beamBetweenShipsChooseFirstShipInterface;

    private Memory memory;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("executeOrdersDecision", this);
    }

    private void initializeForDecisions() {
        mainDecisionInterface = new MainDecisionInterface();
        attemptMissionInterface = new AttemptMissionInterface();
        moveShipSelectionInterface = new MoveShipSelectionInterface();
        beamSelectionInterface = new BeamSelectionInterface();
        beamFromMissionChooseShipInterface = new BeamFromMissionChooseShipInterface();
        beamToMissionChooseShipInterface = new BeamToMissionChooseShipInterface();
        beamBetweenShipsChooseFirstShipInterface = new BeamBetweenShipsChooseFirstShipInterface();
    }

    private void goToDecisionInterface(DecisionInterface decisionInterface) {
        if (this.decisionInterface != null) {
            this.decisionInterface.cleanupDecision();
        }
        this.decisionInterface = decisionInterface;
        if (this.decisionInterface != null) {
            this.decisionInterface.proceedToDecision();
        }
    }

    private void enableButton(Button button, boolean enabled) {
        button.setDisabled(!enabled);
        button.setTouchable(enabled ? Touchable.enabled : Touchable.disabled);
    }

    @Override
    public void processNewDecision(ObjectMap<String, String> decisionData) {
        if (mainDecisionInterface == null) {
            initializeForDecisions();
        }

        memory = new Memory(decisionData);

        goToDecisionInterface(mainDecisionInterface);
    }

    @Override
    protected void processSystem() {

    }

    private interface DecisionInterface {
        void proceedToDecision();

        void cleanupDecision();
    }

    private class MainDecisionInterface implements DecisionInterface {
        private Table table;
        private TextButton executeOrderButton;
        private TextButton attemptMissionButton;
        private TextButton beamButton;
        private TextButton moveShipButton;
        private TextButton passButton;

        private SelectionState selectionState;

        public MainDecisionInterface() {
            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderRequirements.createOrderRequirements(
                    authenticationHolderSystem.getUsername(), cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(executeOrderButton, selected.size == 1);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            executeOrderButton = new TextButton("Execute order", stageSystem.getSkin(), UISettings.mainButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            executeOrderButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            executeOrder();
                        }
                    });
            verticalGroup.addActor(executeOrderButton);

            attemptMissionButton = new TextButton("Attempt mission", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            attemptMissionButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            attemptMission();
                        }
                    });
            verticalGroup.addActor(attemptMissionButton);

            beamButton = new TextButton("Beam", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            beamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            initiateBeam();
                        }
                    });
            verticalGroup.addActor(beamButton);

            moveShipButton = new TextButton("Move ship", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            moveShipButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            initiateMoveShip();
                        }
                    });
            verticalGroup.addActor(moveShipButton);

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
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(executeOrderButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Execute order");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }

        private void executeOrder() {
            Entity selected = selectionSystem.getSelectedEntities().iterator().next();
            int serverEntityId = selected.getComponent(ServerCardReferenceComponent.class).getEntityId();
            Entity usedCardEntity = world.getEntity(serverEntityId);
            String entityId = usedCardEntity.getComponent(ServerEntityComponent.class).getEntityId();

            int orderIndex = OrderRequirements.findUsableOrderIndex(usedCardEntity, memory,
                    cardAbilitySystem, conditionResolverSystem);

            ObjectMap<String, String> parameters = new ObjectMap<>();
            parameters.put("action", "use");
            parameters.put("cardId", entityId);
            parameters.put("orderIndex", String.valueOf(orderIndex));
            clientDecisionSystem.executeDecision(parameters);

            goToDecisionInterface(null);
        }

        private void attemptMission() {
            goToDecisionInterface(attemptMissionInterface);
        }

        private void initiateBeam() {
            goToDecisionInterface(beamSelectionInterface);
        }

        private void initiateMoveShip() {
            goToDecisionInterface(moveShipSelectionInterface);
        }

        private void pass() {
            ObjectMap<String, String> parameters = new ObjectMap<>();
            parameters.put("action", "pass");
            clientDecisionSystem.executeDecision(parameters);
            goToDecisionInterface(null);
        }
    }

    private class AttemptMissionInterface implements DecisionInterface {
        private Table table;
        private TextButton goOnMissionButton;
        private TextButton cancelButton;

        private SelectionState selectionState;

        public AttemptMissionInterface() {
            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderRequirements.createAttemptMissionRequirements(
                    authenticationHolderSystem.getUsername(), cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(goOnMissionButton, selected.size == 1);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            goOnMissionButton = new TextButton("Go on mission", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            goOnMissionButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            goOnMission();
                        }
                    });
            verticalGroup.addActor(goOnMissionButton);

            cancelButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelMission();
                        }
                    });
            verticalGroup.addActor(cancelButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        private void goOnMission() {
            Entity missionEntity = getServerEntity(selectionSystem.getSelectedEntities().iterator().next());
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(missionEntity);
            if (cardDefinition.getMissionType() == MissionType.Planet) {
                goToDecisionInterface(new ChooseShipsAttemptingMissionInterface(missionEntity));
            } else {
                String missionId = missionEntity.getComponent(ServerEntityComponent.class).getEntityId();

                ObjectMap<String, String> parameters = new ObjectMap<>();
                parameters.put("action", "attemptPlanetMission");
                parameters.put("missionId", missionId);
                clientDecisionSystem.executeDecision(parameters);

                goToDecisionInterface(null);
            }
        }

        private void cancelMission() {
            goToDecisionInterface(mainDecisionInterface);
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(goOnMissionButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose mission to attempt");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }
    }

    private class ChooseShipsAttemptingMissionInterface implements DecisionInterface {
        private Table table;
        private TextButton goOnMissionButton;
        private TextButton cancelButton;

        private SelectionState selectionState;
        private Entity missionEntity;

        public ChooseShipsAttemptingMissionInterface(Entity missionEntity) {
            this.missionEntity = missionEntity;
            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderRequirements.createAttemptMissionShipsRequirements(
                    missionEntity, cardFilteringSystem);
            CardFilter shipMissionAffiliationsRequirement = OrderRequirements.createMissionAffiliationsShipRequirements(cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            boolean valid = false;
                            for (Entity entity : selected) {
                                Entity shipEntity = getServerEntity(entity);
                                if (shipMissionAffiliationsRequirement.accepts(null, null, shipEntity))
                                    valid = true;
                            }

                            enableButton(goOnMissionButton, valid && selected.size > 0);
                        }
                    }, Integer.MAX_VALUE);

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            goOnMissionButton = new TextButton("Go on mission", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            goOnMissionButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            goOnMission();
                        }
                    });
            verticalGroup.addActor(goOnMissionButton);

            cancelButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelMission();
                        }
                    });
            verticalGroup.addActor(cancelButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        private void goOnMission() {
            String missionId = missionEntity.getComponent(ServerEntityComponent.class).getEntityId();

            Array<String> ids = new Array<>();
            for (Entity selectedEntity : selectionSystem.getSelectedEntities()) {
                ids.add(getServerEntity(selectedEntity).getComponent(ServerEntityComponent.class).getEntityId());
            }

            ObjectMap<String, String> parameters = new ObjectMap<>();
            parameters.put("action", "attemptSpaceMission");
            parameters.put("missionId", missionId);
            parameters.put("shipIds", StringUtils.merge(ids, ","));
            clientDecisionSystem.executeDecision(parameters);

            goToDecisionInterface(null);
        }

        private void cancelMission() {
            goToDecisionInterface(mainDecisionInterface);
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(goOnMissionButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose ships to attempt mission");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }
    }

    private class MoveShipSelectionInterface implements DecisionInterface {
        private Table table;
        private TextButton acceptShipButton;
        private TextButton cancelMoveButton;

        private SelectionState selectionState;

        public MoveShipSelectionInterface() {
            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderMoveRequirements.createMoveShipRequirements(
                    authenticationHolderSystem.getUsername(), cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(acceptShipButton, selected.size == 1);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            acceptShipButton = new TextButton("Select", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            acceptShipButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            selectMoveToMission();
                        }
                    });
            verticalGroup.addActor(acceptShipButton);

            cancelMoveButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelMoveButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelMoveShip();
                        }
                    });
            verticalGroup.addActor(cancelMoveButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        private void selectMoveToMission() {
            Entity selected = selectionSystem.getSelectedEntities().iterator().next();
            Entity shipEntity = getServerEntity(selected);
            goToDecisionInterface(new MoveShipSelectMissionInterface(shipEntity));
        }

        private void cancelMoveShip() {
            goToDecisionInterface(mainDecisionInterface);
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(acceptShipButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose ship to move");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }
    }

    private class MoveShipSelectMissionInterface implements DecisionInterface {
        private Table table;
        private TextButton moveShipButton;
        private TextButton cancelMoveButton;

        private SelectionState selectionState;

        private Entity shipEntity;

        public MoveShipSelectMissionInterface(Entity shipEntity) {
            this.shipEntity = shipEntity;

            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderMoveRequirements.createMoveShipMissionRequirements(
                    authenticationHolderSystem.getUsername(), shipEntity, cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(moveShipButton, selected.size == 1);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            moveShipButton = new TextButton("Make it so!", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            moveShipButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            moveShipToMission();
                        }
                    });
            verticalGroup.addActor(moveShipButton);

            cancelMoveButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelMoveButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelMoveShip();
                        }
                    });
            verticalGroup.addActor(cancelMoveButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        private void moveShipToMission() {
            String shipId = shipEntity.getComponent(ServerEntityComponent.class).getEntityId();
            Entity missionCardEntity = selectionSystem.getSelectedEntities().iterator().next();
            String missionId = missionCardEntity.getComponent(ServerEntityComponent.class).getEntityId();

            ObjectMap<String, String> parameters = new ObjectMap<>();
            parameters.put("action", "moveShip");
            parameters.put("shipId", shipId);
            parameters.put("missionId", missionId);
            clientDecisionSystem.executeDecision(parameters);

            goToDecisionInterface(null);
        }

        private void cancelMoveShip() {
            goToDecisionInterface(mainDecisionInterface);
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(moveShipButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose mission to move to");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }
    }

    private class BeamSelectionInterface implements DecisionInterface {
        private Table table;
        private TextButton beamFromMissionButton;
        private TextButton beamToMissionButton;
        private TextButton beamBetweenShipsButton;
        private TextButton cancelBeamButton;

        public BeamSelectionInterface() {
            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            beamFromMissionButton = new TextButton("To ship", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            beamFromMissionButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            initiateBeamFromMission();
                        }
                    });
            verticalGroup.addActor(beamFromMissionButton);

            beamToMissionButton = new TextButton("To mission", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            beamToMissionButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            initiateBeamToMission();
                        }
                    });
            verticalGroup.addActor(beamToMissionButton);

            beamBetweenShipsButton = new TextButton("Between ships", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            beamBetweenShipsButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            initiateBeamBetweenShips();
                        }
                    });
            verticalGroup.addActor(beamBetweenShipsButton);

            cancelBeamButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelBeamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelBeam();
                        }
                    });
            verticalGroup.addActor(cancelBeamButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose how to beam");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            promptRenderingSystem.removePrompt();
        }

        private void cancelBeam() {
            goToDecisionInterface(mainDecisionInterface);
        }

        private void initiateBeamFromMission() {
            goToDecisionInterface(beamFromMissionChooseShipInterface);
        }

        private void initiateBeamToMission() {
            goToDecisionInterface(beamToMissionChooseShipInterface);
        }

        private void initiateBeamBetweenShips() {
            goToDecisionInterface(beamBetweenShipsChooseFirstShipInterface);
        }
    }

    private class BeamBetweenShipsChooseFirstShipInterface implements DecisionInterface {
        private Table table;
        private TextButton acceptShipButton;
        private TextButton cancelBeamButton;

        private SelectionState selectionState;

        public BeamBetweenShipsChooseFirstShipInterface() {
            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderMoveRequirements.createBeamFromMissionShipRequirements(
                    authenticationHolderSystem.getUsername(), cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(acceptShipButton, selected.size == 1);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            acceptShipButton = new TextButton("Accept", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            acceptShipButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            acceptShip();
                        }
                    });
            verticalGroup.addActor(acceptShipButton);

            cancelBeamButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelBeamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelBeam();
                        }
                    });
            verticalGroup.addActor(cancelBeamButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(acceptShipButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose ship to beam from");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }

        private void acceptShip() {
            Entity selected = selectionSystem.getSelectedEntities().iterator().next();
            Entity shipEntity = getServerEntity(selected);
            goToDecisionInterface(new BeamBetweenShipsChooseSecondShipInterface(shipEntity));
        }

        private void cancelBeam() {
            goToDecisionInterface(mainDecisionInterface);
        }
    }

    private class BeamBetweenShipsChooseSecondShipInterface implements DecisionInterface {
        private Table table;
        private TextButton acceptShipButton;
        private TextButton cancelBeamButton;

        private SelectionState selectionState;

        private Entity firstShipEntity;

        public BeamBetweenShipsChooseSecondShipInterface(Entity firstShipEntity) {
            this.firstShipEntity = firstShipEntity;

            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderMoveRequirements.createBeamSelectAnotherShipRequirements(
                    authenticationHolderSystem.getUsername(), firstShipEntity, cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(acceptShipButton, selected.size == 1);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            acceptShipButton = new TextButton("Accept", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            acceptShipButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            acceptShip();
                        }
                    });
            verticalGroup.addActor(acceptShipButton);

            cancelBeamButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelBeamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelBeam();
                        }
                    });
            verticalGroup.addActor(cancelBeamButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(acceptShipButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose ship to beam to");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }

        private void acceptShip() {
            Entity selected = selectionSystem.getSelectedEntities().iterator().next();
            Entity shipEntity = getServerEntity(selected);
            goToDecisionInterface(new BeamBetweenShipsChooseEntitiesInterface(firstShipEntity, shipEntity));
        }

        private void cancelBeam() {
            goToDecisionInterface(mainDecisionInterface);
        }
    }

    private class BeamBetweenShipsChooseEntitiesInterface implements DecisionInterface {
        private Table table;
        private TextButton beamButton;
        private TextButton cancelBeamButton;

        private SelectionState selectionState;

        private Entity fromShipEntity;
        private Entity toShipEntity;

        public BeamBetweenShipsChooseEntitiesInterface(Entity fromShipEntity, Entity toShipEntity) {
            this.fromShipEntity = fromShipEntity;
            this.toShipEntity = toShipEntity;

            String shipId = fromShipEntity.getComponent(ServerEntityComponent.class).getEntityId();

            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderMoveRequirements.createBeamBetweenShipsRequirements(
                    authenticationHolderSystem.getUsername(),
                    fromShipEntity, toShipEntity, cardFilteringSystem);

            CardFilter onShip = new CardFilter() {
                @Override
                public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                    CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);
                    if (cardInPlay != null)
                        return shipId.equals(cardInPlay.getAttachedToId());
                    return false;
                }
            };

            selectionState = new SelectionState(world, userInputStateEntity, new AndCardFilter(onShip, playRequirementsFilter),
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(beamButton, selected.size > 0);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            beamButton = new TextButton("Beam!", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            beamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            beamSelected();
                        }
                    });
            verticalGroup.addActor(beamButton);

            cancelBeamButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelBeamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelBeam();
                        }
                    });
            verticalGroup.addActor(cancelBeamButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(beamButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose personnel and equipment to beam");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }

        private void beamSelected() {
            String fromShipId = fromShipEntity.getComponent(ServerEntityComponent.class).getEntityId();
            String toShipId = toShipEntity.getComponent(ServerEntityComponent.class).getEntityId();

            Array<String> ids = new Array<>();
            for (Entity selectedEntity : selectionSystem.getSelectedEntities()) {
                ids.add(getServerEntity(selectedEntity).getComponent(ServerEntityComponent.class).getEntityId());
            }

            ObjectMap<String, String> parameters = new ObjectMap<>();
            parameters.put("action", "beamBetweenShips");
            parameters.put("fromShipId", fromShipId);
            parameters.put("toShipId", toShipId);
            parameters.put("beamedId", StringUtils.merge(ids, ","));
            clientDecisionSystem.executeDecision(parameters);

            goToDecisionInterface(null);
        }

        private void cancelBeam() {
            goToDecisionInterface(mainDecisionInterface);
        }
    }

    private class BeamFromMissionChooseShipInterface implements DecisionInterface {
        private Table table;
        private TextButton acceptShipButton;
        private TextButton cancelBeamButton;

        private SelectionState selectionState;

        public BeamFromMissionChooseShipInterface() {
            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderMoveRequirements.createBeamFromMissionShipRequirements(
                    authenticationHolderSystem.getUsername(), cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(acceptShipButton, selected.size == 1);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            acceptShipButton = new TextButton("Accept", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            acceptShipButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            acceptShip();
                        }
                    });
            verticalGroup.addActor(acceptShipButton);

            cancelBeamButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelBeamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelBeam();
                        }
                    });
            verticalGroup.addActor(cancelBeamButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(acceptShipButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose ship to beam to");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }

        private void acceptShip() {
            Entity selected = selectionSystem.getSelectedEntities().iterator().next();
            Entity shipEntity = getServerEntity(selected);
            goToDecisionInterface(new BeamFromMissionChooseEntitiesInterface(shipEntity));
        }

        private void cancelBeam() {
            goToDecisionInterface(mainDecisionInterface);
        }
    }

    private class BeamToMissionChooseShipInterface implements DecisionInterface {
        private Table table;
        private TextButton acceptShipButton;
        private TextButton cancelBeamButton;

        private SelectionState selectionState;

        public BeamToMissionChooseShipInterface() {
            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderMoveRequirements.createBeamToMissionShipRequirements(
                    authenticationHolderSystem.getUsername(), cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(acceptShipButton, selected.size == 1);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            acceptShipButton = new TextButton("Accept", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            acceptShipButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            acceptShip();
                        }
                    });
            verticalGroup.addActor(acceptShipButton);

            cancelBeamButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelBeamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelBeam();
                        }
                    });
            verticalGroup.addActor(cancelBeamButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(acceptShipButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose ship to beam from");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }

        private void acceptShip() {
            Entity selected = selectionSystem.getSelectedEntities().iterator().next();
            Entity shipEntity = getServerEntity(selected);
            goToDecisionInterface(new BeamToMissionChooseEntitiesInterface(shipEntity));
        }

        private void cancelBeam() {
            goToDecisionInterface(mainDecisionInterface);
        }
    }

    private class BeamFromMissionChooseEntitiesInterface implements DecisionInterface {
        private Table table;
        private TextButton beamButton;
        private TextButton cancelBeamButton;

        private SelectionState selectionState;

        private Entity shipEntity;

        public BeamFromMissionChooseEntitiesInterface(Entity shipEntity) {
            this.shipEntity = shipEntity;

            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderMoveRequirements.createBeamFromMissionRequirements(
                    authenticationHolderSystem.getUsername(),
                    shipEntity, cardFilteringSystem);

            selectionState = new SelectionState(world, userInputStateEntity, playRequirementsFilter,
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(beamButton, selected.size > 0);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            beamButton = new TextButton("Beam!", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            beamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            beamSelected();
                        }
                    });
            verticalGroup.addActor(beamButton);

            cancelBeamButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelBeamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelBeam();
                        }
                    });
            verticalGroup.addActor(cancelBeamButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(beamButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose personnel and equipment to beam");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }

        private void beamSelected() {
            String shipId = shipEntity.getComponent(ServerEntityComponent.class).getEntityId();

            Array<String> ids = new Array<>();
            for (Entity selectedEntity : selectionSystem.getSelectedEntities()) {
                ids.add(getServerEntity(selectedEntity).getComponent(ServerEntityComponent.class).getEntityId());
            }

            ObjectMap<String, String> parameters = new ObjectMap<>();
            parameters.put("action", "beamFromMission");
            parameters.put("shipId", shipId);
            parameters.put("beamedId", StringUtils.merge(ids, ","));
            clientDecisionSystem.executeDecision(parameters);

            goToDecisionInterface(null);
        }

        private void cancelBeam() {
            goToDecisionInterface(mainDecisionInterface);
        }
    }

    private class BeamToMissionChooseEntitiesInterface implements DecisionInterface {
        private Table table;
        private TextButton beamButton;
        private TextButton cancelBeamButton;

        private SelectionState selectionState;

        private Entity shipEntity;

        public BeamToMissionChooseEntitiesInterface(Entity shipEntity) {
            this.shipEntity = shipEntity;

            String shipId = shipEntity.getComponent(ServerEntityComponent.class).getEntityId();

            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = OrderMoveRequirements.createBeamToMissionRequirements(
                    authenticationHolderSystem.getUsername(),
                    shipEntity, cardFilteringSystem);

            CardFilter onShip = new CardFilter() {
                @Override
                public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                    CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);
                    if (cardInPlay != null)
                        return shipId.equals(cardInPlay.getAttachedToId());
                    return false;
                }
            };

            selectionState = new SelectionState(world, userInputStateEntity, new AndCardFilter(onShip, playRequirementsFilter),
                    new SelectionCallback() {
                        @Override
                        public void selectionChanged(ObjectSet<Entity> selected) {
                            enableButton(beamButton, selected.size > 0);
                        }
                    });

            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            beamButton = new TextButton("Beam!", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            beamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            beamSelected();
                        }
                    });
            verticalGroup.addActor(beamButton);

            cancelBeamButton = new TextButton("Cancel", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
                @Override
                public float getPrefWidth() {
                    return 200;
                }
            };
            cancelBeamButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            cancelBeam();
                        }
                    });
            verticalGroup.addActor(cancelBeamButton);

            table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
        }

        @Override
        public void proceedToDecision() {
            Stage stage = stageSystem.getStage();

            selectionState.markSelectableCards();
            selectionSystem.startSelection(selectionState);

            enableButton(beamButton, false);

            stage.addActor(table);

            promptRenderingSystem.setPrompt("Choose personnel and equipment to beam");
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();

            promptRenderingSystem.removePrompt();
        }

        private void beamSelected() {
            String shipId = shipEntity.getComponent(ServerEntityComponent.class).getEntityId();

            Array<String> ids = new Array<>();
            for (Entity selectedEntity : selectionSystem.getSelectedEntities()) {
                ids.add(getServerEntity(selectedEntity).getComponent(ServerEntityComponent.class).getEntityId());
            }

            ObjectMap<String, String> parameters = new ObjectMap<>();
            parameters.put("action", "beamToMission");
            parameters.put("shipId", shipId);
            parameters.put("beamedId", StringUtils.merge(ids, ","));
            clientDecisionSystem.executeDecision(parameters);

            goToDecisionInterface(null);
        }

        private void cancelBeam() {
            goToDecisionInterface(mainDecisionInterface);
        }
    }

    private Entity getServerEntity(Entity renderedEntity) {
        int serverEntityId = renderedEntity.getComponent(ServerCardReferenceComponent.class).getEntityId();
        return world.getEntity(serverEntityId);
    }
}
