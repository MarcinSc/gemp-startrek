package com.gempukku.startrek.game.decision.ui;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickableComponent;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickingSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;

public class CardManipulation {
    private CameraSystem cameraSystem;
    private SpawnSystem spawnSystem;
    private ShapePickingSystem shapePickingSystem;
    private TransformSystem transformSystem;

    private Array<CardContainer> cardContainers = new Array<>();

    private Entity userInputStateEntity;
    private String cameraName;
    private String shapePickingMask;
    private String shapeDroppingMask;

    private Entity draggedEntity;
    private Vector2 distanceFromCardCenter;

    private boolean layoutDirty = true;

    public void addCardContainer(Entity... cards) {
        CardContainer cardContainer = new CardContainer(spawnSystem);
        cardContainers.add(cardContainer);
        for (Entity card : cards) {
            card.getComponent(ShapePickableComponent.class).getPickingMask().add(shapePickingMask);
            cardContainer.addCard(card);
        }
    }

    private void createShadowCard() {
        spawnSystem.spawnEntity("game/card/card-full-selection.template");
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

            } else {

            }
        }

        if (layoutDirty) {

        }
    }

    private void processPickedCard(Camera camera, Entity pickedCard) {
        for (CardContainer cardContainer : cardContainers) {
            cardContainer.removeCard(pickedCard);
        }

        Vector3 tmpVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 mousePosition = camera.unproject(tmpVector);
        draggedEntity = pickedCard;
        Vector3 cardPosition = transformSystem.getResolvedTransform(pickedCard).getTranslation(tmpVector);
        distanceFromCardCenter = new Vector2(mousePosition.x - cardPosition.x, mousePosition.y - cardPosition.y);

        layoutDirty = true;
    }
}
