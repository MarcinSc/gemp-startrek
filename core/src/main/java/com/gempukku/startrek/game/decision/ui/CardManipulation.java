package com.gempukku.startrek.game.decision.ui;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickableComponent;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickingSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;

public class CardManipulation {
    private CameraSystem cameraSystem;
    private SpawnSystem spawnSystem;
    private ShapePickingSystem shapePickingSystem;

    private Array<CardContainer> cardContainers = new Array<>();

    private String cameraName;
    private String shapePickingMask;
    private String shapeDroppingMask;

    private Entity draggedEntity;

    public void addCardContainer(CardContainer cardContainer, Entity... cards) {
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
        Camera camera = cameraSystem.getCamera(cameraName);
        Vector3 unprojected = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

    }
}
