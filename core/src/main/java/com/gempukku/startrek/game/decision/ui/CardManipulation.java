package com.gempukku.startrek.game.decision.ui;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickableComponent;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickingSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.startrek.game.layout.OverlappingCardsLayout;

public class CardManipulation implements Disposable {
    private World world;
    private CameraSystem cameraSystem;
    private SpawnSystem spawnSystem;
    private ShapePickingSystem shapePickingSystem;
    private TransformSystem transformSystem;
    private HierarchySystem hierarchySystem;

    private Array<CardContainer> cardContainers = new Array<>();

    private Entity userInputStateEntity;
    private float dragScale;
    private String cameraName;
    private String shapePickingMask;
    private String shapeDroppingMask;

    private Entity draggedEntity;
    private Vector2 distanceFromCardCenter;
    private Entity highlightedEntity;
    private Entity highlightEntity;

    private boolean layoutDirty = true;

    private Matrix4 tmpMatrix4 = new Matrix4();

    public CardManipulation(World world, CameraSystem cameraSystem, SpawnSystem spawnSystem,
                            ShapePickingSystem shapePickingSystem, TransformSystem transformSystem,
                            HierarchySystem hierarchySystem,
                            Entity userInputStateEntity, float dragScale,
                            String cameraName, String shapePickingMask, String shapeDroppingMask) {
        this.world = world;
        this.cameraSystem = cameraSystem;
        this.spawnSystem = spawnSystem;
        this.shapePickingSystem = shapePickingSystem;
        this.transformSystem = transformSystem;
        this.hierarchySystem = hierarchySystem;
        this.userInputStateEntity = userInputStateEntity;
        this.dragScale = dragScale;
        this.cameraName = cameraName;
        this.shapePickingMask = shapePickingMask;
        this.shapeDroppingMask = shapeDroppingMask;
    }

    public void addCardContainer(CardContainerSettings cardContainerSettings, Array<Entity> cards) {
        CardContainer cardContainer = new CardContainer(world, spawnSystem, cardContainerSettings);
        cardContainers.add(cardContainer);
        for (Entity card : cards) {
            card.getComponent(ShapePickableComponent.class).getPickingMask().add(shapePickingMask);
            cardContainer.addCard(card);
        }
    }

    public Array<Entity> getContainerCards(int containerIndex) {
        return cardContainers.get(containerIndex).getCards();
    }

    public void update() {
        UserInputStateComponent inputState = userInputStateEntity.getComponent(UserInputStateComponent.class);

        Camera camera = cameraSystem.getCamera(cameraName);
        if (draggedEntity == null) {
            if (inputState.getSignals().contains("selectToggle")) {
                Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
                Entity pickedCard = shapePickingSystem.pickEntity(pickRay, shapePickingMask, arg -> true);
                if (pickedCard != null) {
                    processPickedCard(camera, pickedCard);
                }
            }
        } else {
            if (inputState.getStates().contains("selectDrag")) {
                Vector3 mousePosition = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                Entity closestShadow = shapePickingSystem.findClosest(mousePosition, shapeDroppingMask, arg -> true);
                if (closestShadow != null) {
                    processDraggingCard(closestShadow);
                    transformSystem.setTransform(draggedEntity,
                            tmpMatrix4.idt().
                                    translate(mousePosition.x + distanceFromCardCenter.x, 98, mousePosition.z + distanceFromCardCenter.y).
                                    scl(dragScale, 1, dragScale));
                }
            } else {
                processDropCard();
            }
        }

        if (layoutDirty) {
            for (CardContainer cardContainer : cardContainers) {
                Array<Entity> cards = cardContainer.getCards();

                CardContainerSettings settings = cardContainer.getCardContainerSettings();

                float scale = settings.getScale();
                OverlappingCardsLayout.layoutCards(transformSystem, settings.getStartupTransform(), cards,
                        scale * settings.getCardWidth(), scale * settings.getCardHeight(), scale,
                        settings.getContainerWidth(), settings.getContainerHeight(), settings.getMinOverlapPerc(),
                        settings.isVertical());
            }
        }
    }

    private void processDropCard() {
        if (draggedEntity != null) {
            for (CardContainer cardContainer : cardContainers) {
                cardContainer.insertCardAfter(draggedEntity, highlightedEntity);
            }
            world.deleteEntity(highlightEntity);
            draggedEntity = null;
            highlightedEntity = null;
            highlightEntity = null;

            layoutDirty = true;
        }
    }

    private void processDraggingCard(Entity closestShadow) {
        if (highlightedEntity != closestShadow) {
            if (highlightEntity != null) {
                world.deleteEntity(highlightEntity);
            }
            highlightEntity = createShadowHighlight();
            hierarchySystem.addHierarchy(closestShadow, highlightEntity);
            highlightedEntity = closestShadow;
        }
    }

    private Entity createShadowHighlight() {
        return spawnSystem.spawnEntity("game/ui/card-shadow-render.template");
    }

    private void processPickedCard(Camera camera, Entity pickedCard) {
        for (CardContainer cardContainer : cardContainers) {
            cardContainer.removeCard(pickedCard);
        }

        Vector3 tmpVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 mousePosition = camera.unproject(tmpVector);
        draggedEntity = pickedCard;
        Vector3 cardPosition = transformSystem.getResolvedTransform(pickedCard).getTranslation(tmpVector);
        distanceFromCardCenter = new Vector2(mousePosition.x - cardPosition.x, mousePosition.z - cardPosition.z);

        layoutDirty = true;
    }

    @Override
    public void dispose() {
        processDropCard();
        for (CardContainer cardContainer : cardContainers) {
            for (Entity card : cardContainer.getCards()) {
                world.deleteEntity(card);
            }
        }
    }
}
