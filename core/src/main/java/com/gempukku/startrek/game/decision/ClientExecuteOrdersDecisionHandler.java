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
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.PlayRequirements;
import com.gempukku.startrek.game.card.ServerCardReferenceComponent;
import com.gempukku.startrek.game.filter.AndCardFilter;
import com.gempukku.startrek.game.filter.CardFilter;
import com.gempukku.startrek.game.filter.CardFilterResolverSystem;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.CardInPlayComponent;

public class ClientExecuteOrdersDecisionHandler extends BaseSystem implements DecisionHandler {
    private ClientDecisionSystem clientDecisionSystem;
    private StageSystem stageSystem;
    private SelectionSystem selectionSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private CardFilterResolverSystem cardFilterResolverSystem;
    private PlayerResolverSystem playerResolverSystem;

    private DecisionInterface decisionInterface;

    private MainDecisionInterface mainDecisionInterface;
    private MoveShipSelectionInterface moveShipSelectionInterface;
    private BeamSelectionInterface beamSelectionInterface;
    private BeamFromMissionChooseShipInterface beamFromMissionChooseShipInterface;
    private BeamToMissionChooseShipInterface beamToMissionChooseShipInterface;
    private BeamBetweenShipsChooseFirstShipInterface beamBetweenShipsChooseFirstShipInterface;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("executeOrdersDecision", this);
    }

    private void initializeForDecisions() {
        mainDecisionInterface = new MainDecisionInterface();
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
        private TextButton beamButton;
        private TextButton moveShipButton;
        private TextButton passButton;

        public MainDecisionInterface() {
            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

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

            stage.addActor(table);
        }

        @Override
        public void cleanupDecision() {
            table.remove();
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

    private class MoveShipSelectionInterface implements DecisionInterface {
        private Table table;
        private TextButton acceptShipButton;
        private TextButton cancelMoveButton;

        private SelectionState selectionState;

        public MoveShipSelectionInterface() {
            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = PlayRequirements.createMoveShipRequirements(
                    authenticationHolderSystem.getUsername(), cardFilterResolverSystem);

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

            cancelMoveButton = new TextButton("Pass", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();
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
            CardFilter playRequirementsFilter = PlayRequirements.createMoveShipMissionRequirements(
                    authenticationHolderSystem.getUsername(), shipEntity, cardFilterResolverSystem);

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

            cancelMoveButton = new TextButton("Pass", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();
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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
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
            CardFilter playRequirementsFilter = PlayRequirements.createBeamFromMissionShipRequirements(
                    authenticationHolderSystem.getUsername(), cardFilterResolverSystem);

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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();
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
            CardFilter playRequirementsFilter = PlayRequirements.createBeamSelectAnotherShipRequirements(
                    authenticationHolderSystem.getUsername(), firstShipEntity, cardFilterResolverSystem);

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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();
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
            CardFilter playRequirementsFilter = PlayRequirements.createBeamBetweenShipsRequirements(
                    authenticationHolderSystem.getUsername(),
                    fromShipEntity, toShipEntity, cardFilterResolverSystem);

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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();
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
            CardFilter playRequirementsFilter = PlayRequirements.createBeamFromMissionShipRequirements(
                    authenticationHolderSystem.getUsername(), cardFilterResolverSystem);

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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();
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
            CardFilter playRequirementsFilter = PlayRequirements.createBeamToMissionShipRequirements(
                    authenticationHolderSystem.getUsername(), cardFilterResolverSystem);

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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();
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
            CardFilter playRequirementsFilter = PlayRequirements.createBeamFromMissionRequirements(
                    authenticationHolderSystem.getUsername(),
                    shipEntity, cardFilterResolverSystem);

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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();
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
            CardFilter playRequirementsFilter = PlayRequirements.createBeamToMissionRequirements(
                    authenticationHolderSystem.getUsername(),
                    shipEntity, cardFilterResolverSystem);

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
        }

        @Override
        public void cleanupDecision() {
            table.remove();
            selectionState.cleanup();
            selectionSystem.finishSelection();
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
