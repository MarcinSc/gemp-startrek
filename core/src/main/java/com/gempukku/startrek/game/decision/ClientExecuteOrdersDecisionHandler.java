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
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.FaceDownCardInMissionComponent;
import com.gempukku.startrek.game.zone.FaceUpCardInMissionComponent;

public class ClientExecuteOrdersDecisionHandler extends BaseSystem implements DecisionHandler {
    private ClientDecisionSystem clientDecisionSystem;
    private StageSystem stageSystem;
    private SelectionSystem selectionSystem;
    private AuthenticationHolderSystem authenticationHolderSystem;
    private CardFilterResolverSystem cardFilterResolverSystem;
    private PlayerResolverSystem playerResolverSystem;

    private DecisionInterface decisionInterface;

    private MainDecisionInterface mainDecisionInterface;
    private BeamSelectionInterface beamSelectionInterface;
    private BeamFromMissionChooseShipInterface beamFromMissionChooseShipInterface;
    private BeamToMissionChooseShipInterface beamToMissionChooseShipInterface;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("executeOrdersDecision", this);
    }

    private void initializeForDecisions() {
        mainDecisionInterface = new MainDecisionInterface();
        beamSelectionInterface = new BeamSelectionInterface();
        beamFromMissionChooseShipInterface = new BeamFromMissionChooseShipInterface();
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

        private void pass() {
            ObjectMap<String, String> parameters = new ObjectMap<>();
            parameters.put("action", "pass");
            clientDecisionSystem.executeDecision(parameters);
            goToDecisionInterface(null);
        }
    }

    protected class BeamSelectionInterface implements DecisionInterface {
        private Table table;
        private TextButton beamFromMissionButton;
        private TextButton beamToMissionButton;
        private TextButton beamBetweenShipsButton;
        private TextButton cancelBeamButton;

        public BeamSelectionInterface() {
            table = new Table();
            table.setFillParent(true);

            VerticalGroup verticalGroup = new VerticalGroup();

            beamFromMissionButton = new TextButton("From mission", stageSystem.getSkin(), UISettings.alternativeButtonStyle) {
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
            // TODO
        }

        private void initiateBeamBetweenShips() {
            // TODO
        }
    }

    private class BeamFromMissionChooseShipInterface implements DecisionInterface {
        private Table table;
        private TextButton acceptShipButton;
        private TextButton cancelBeamButton;

        private SelectionState selectionState;

        public BeamFromMissionChooseShipInterface() {
            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = PlayRequirements.createBeamFromMissionShipRequirements(authenticationHolderSystem.getUsername(),
                    cardFilterResolverSystem);

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
            CardFilter playRequirementsFilter = PlayRequirements.createBeamToMissionShipRequirements(authenticationHolderSystem.getUsername(),
                    cardFilterResolverSystem);

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

            FaceUpCardInMissionComponent ship = shipEntity.getComponent(FaceUpCardInMissionComponent.class);
            Entity playerEntity = playerResolverSystem.findPlayerEntity(ship.getMissionOwner());
            Entity missionEntity = MissionOperations.findMission(world, playerEntity, ship.getMissionIndex());

            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = PlayRequirements.createBeamFromMissionRequirements(authenticationHolderSystem.getUsername(),
                    missionEntity, shipEntity, cardFilterResolverSystem);

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
            FaceUpCardInMissionComponent ship = shipEntity.getComponent(FaceUpCardInMissionComponent.class);
            Entity playerEntity = playerResolverSystem.findPlayerEntity(ship.getMissionOwner());
            Entity missionEntity = MissionOperations.findMission(world, playerEntity, ship.getMissionIndex());

            Entity userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);
            CardFilter playRequirementsFilter = PlayRequirements.createBeamToMissionRequirements(authenticationHolderSystem.getUsername(),
                    missionEntity, shipEntity, cardFilterResolverSystem);

            CardFilter onShip = new CardFilter() {
                @Override
                public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                    FaceDownCardInMissionComponent faceDownCard = cardEntity.getComponent(FaceDownCardInMissionComponent.class);
                    if (faceDownCard != null)
                        return shipId.equals(faceDownCard.getOnShipId());
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
